package org.example.docmeet.appointment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.docmeet.appointment.enums.AppointmentStateEnum;
import org.example.docmeet.authorization.IsAdmin;
import org.example.docmeet.doctor.Doctor;
import org.example.docmeet.doctor.DoctorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Service
@Slf4j
public class AppointmentService {

    private final AppointmentRepository repository;
    private final DoctorRepository doctorRepository;

    @IsAdmin
    public Flux<Appointment> findAll() {
        return repository.findAll().log();
    }

    @PreAuthorize("hasAnyRole({'client_admin', 'client_doctor'})")
    public Mono<Page<Appointment>> findByDoctorId(Integer doctorId, Pageable pageable) {

        AtomicInteger size = new AtomicInteger();

        return repository.findByDoctorId(doctorId, pageable)
                         .log()
                         .doOnNext(ignore -> size.getAndIncrement())
                         .skip(pageable.getOffset())
                         .take(pageable.getPageSize())
                         .collectList()
                         .map(appointments -> new PageImpl<>(appointments, pageable, size.get()));
    }

    public Flux<Appointment> findByPatientId(Integer patientId) {
        return repository.findByPatientId(patientId).log();
    }

    public Mono<Page<Appointment>> findByPatientId(Integer patientId, Pageable pageable) {
        AtomicInteger size = new AtomicInteger();

        return repository.findByPatientId(patientId, pageable)
                         .log()
                         .doOnNext(ignore -> size.getAndIncrement())
                         .skip(pageable.getOffset())
                         .take(pageable.getPageSize())
                         .collectList()
                         .map(appointments -> new PageImpl<>(appointments, pageable, appointments.size()));
    }

    public Mono<Page<Appointment>> findAvailableBySpecialityId(Integer specialityId,
                                                               Pageable pageable,
                                                               LocalDateTime from,
                                                               LocalDateTime to) {
        LocalDateTime now = LocalDateTime.now();

        from = (from == null) ? now : from;
        to = (to == null) ? from.plusDays(7) : to;

        if (from.isBefore(now)) {
            return Mono.error(new IllegalArgumentException("The 'from' date cannot be before today."));
        }

        if (to.isBefore(from)) {
            return Mono.error(new IllegalArgumentException("The 'to' date cannot be before the 'from' date."));
        }

        final LocalDateTime finalFrom = from;
        final LocalDateTime finalTo = to;

        log.info("Finding available appointments for params: {}, {}, {}, {}",
                 specialityId,
                 pageable,
                 finalFrom,
                 finalTo);

        AtomicInteger size = new AtomicInteger();
        AtomicInteger logCounter = new AtomicInteger();

        return doctorRepository.findBySpecialityId(specialityId)
                               .log()
                               .map(Doctor::getId)
                               .collectList()
                               .flatMapMany(ids -> repository.findAll()
                                                             .filter(appointment ->
                                                                             ids.contains(appointment.getDoctorId()) &&
                                                                             appointment.getDateTime()
                                                                                        .isAfter(finalFrom) &&
                                                                             appointment.getDateTime()
                                                                                        .isBefore(finalTo) &&
                                                                             appointment.getState() ==
                                                                             AppointmentStateEnum.AVAILABLE))
                               .doOnNext(ignore -> size.getAndIncrement())
                               .sort(Comparator.comparing(Appointment::getDateTime))
                               .skip(pageable.getOffset())
                               .take(pageable.getPageSize())
                               .doOnNext(appointment -> {
                                   logCounter.getAndIncrement();
                                   log.info("Appointment {} [{}/{}] found for params: {}, {}, {}, {}",
                                            appointment,
                                            logCounter.get(),
                                            pageable.getPageSize(),
                                            specialityId,
                                            pageable,
                                            finalFrom,
                                            finalTo);
                               })
                               .doOnError(e -> log.error(
                                       "Error while finding available appointments for specialityId {}",
                                       specialityId,
                                       e))
                               .collectList()
                               .map(appointments -> new PageImpl<>(appointments, pageable, size.get()));
    }

    public Flux<Appointment> findByState(AppointmentStateEnum state, Pageable pageable) {
        return repository.findByState(state, pageable).log();
    }

    public Mono<Page<Appointment>> findByDoctorIdAndStateAvailable(Integer doctorId, Pageable pageable) {
        AtomicInteger size = new AtomicInteger();

        return repository.findByDoctorIdAndState(doctorId, AppointmentStateEnum.AVAILABLE, pageable)
                         .log()
                         .doOnNext(appointment -> {
                             log.info("Found available appointment {} for doctorId {}", appointment, doctorId);
                             size.getAndIncrement();
                         })
                         .skip(pageable.getOffset())
                         .take(pageable.getPageSize())
                         .collectList()
                         .map(appointments -> new PageImpl<>(appointments, pageable, size.get()));
    }

    @IsAdmin
    public Mono<Appointment> findById(Integer appointmentId) {
        return repository.findById(appointmentId).log();
    }

    public Mono<Appointment> save(Appointment appointment) {
        return repository.save(appointment)
                         .log()
                         .doOnError(e -> log.error("Error while saving appointment {}", appointment, e));
    }

    public Mono<Appointment> update(Integer appointmentId, Appointment appointment) {
        return repository.findById(appointmentId)
                         .switchIfEmpty(Mono.error(new IllegalArgumentException("Appointment with id %s not found".formatted(
                                 appointmentId))))
                         .doOnError(e -> log.error("Error while updating appointment {}", appointment, e))
                         .flatMap(existingAppointment -> {
                             Appointment updatedAppointment = Appointment.builder()
                                                                         .id(appointmentId)
                                                                         .doctorId(appointment.getDoctorId() != null ?
                                                                                           appointment.getDoctorId() :
                                                                                           existingAppointment.getDoctorId())
                                                                         .patientId(appointment.getPatientId() != null ?
                                                                                            appointment.getPatientId() :
                                                                                            existingAppointment.getPatientId())
                                                                         .dateTime(appointment.getDateTime() != null ?
                                                                                           appointment.getDateTime() :
                                                                                           existingAppointment.getDateTime())
                                                                         .type(appointment.getType() != null ?
                                                                                       appointment.getType() :
                                                                                       existingAppointment.getType())
                                                                         .state(appointment.getState() != null ?
                                                                                        appointment.getState() :
                                                                                        existingAppointment.getState())
                                                                         .address(appointment.getAddress() != null ?
                                                                                          appointment.getAddress() :
                                                                                          existingAppointment.getAddress())
                                                                         .note(appointment.getNote() != null ?
                                                                                       appointment.getNote() :
                                                                                       existingAppointment.getNote())
                                                                         .patientNote(
                                                                                 appointment.getPatientNote() != null ?
                                                                                         appointment.getPatientNote() :
                                                                                         existingAppointment.getPatientNote())
                                                                         .rating(appointment.getRating() != null ?
                                                                                         appointment.getRating() :
                                                                                         existingAppointment.getRating())
                                                                         .build();
                             return repository.save(updatedAppointment);
                         })
                         .log();
    }

    public Mono<Void> deletePatientIdFromAppointment(Integer appointmentId) {
        return repository.findById(appointmentId)
                         .switchIfEmpty(Mono.error(new IllegalArgumentException("Appointment with id %s not found".formatted(
                                 appointmentId))))
                         .doOnSuccess(appointment -> appointment.setPatientId(null))
                         .flatMap(repository::save)
                .then();
    }

    public Mono<Void> deleteById(Integer appointmentId) {
        return repository.existsById(appointmentId)
                         .flatMap(bool -> bool ? Mono.empty() : Mono.error(new IllegalArgumentException(
                                 "Cannot delete. Appointment with id %s does not exist".formatted(appointmentId))))
                .then(repository.deleteById(appointmentId))
                .log();
    }
}
