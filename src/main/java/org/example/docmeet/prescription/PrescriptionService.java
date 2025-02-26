package org.example.docmeet.prescription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.docmeet.authorization.IsAdmin;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrescriptionService {

    private final PrescriptionRepository repository;

    @IsAdmin
    public Flux<Prescription> findAll() {
        return repository.findAll();
    }

    @IsAdmin
    public Mono<Prescription> findById(Integer id) {
        return repository.findById(id)
                         .switchIfEmpty(Mono.error(new NoResourceFoundException("Prescription with id %s not found".formatted(
                                 id))))
                         .doOnError(e -> log.error("Error while finding prescription with id {}", id, e))
                         .doOnSuccess(prescription -> log.info("Found prescription {} with id {}", prescription, id));
    }

    public Mono<Prescription> findByAppointmentId(Integer appointmentId) {
        return repository.findByAppointmentId(appointmentId)
                         .switchIfEmpty(Mono.error(new NoResourceFoundException(
                                 "Prescription with appointment id %s not found".formatted(appointmentId))))
                         .doOnError(e -> log.error("Error while finding prescription with appointment id {}",
                                                   appointmentId,
                                                   e))
                         .doOnSuccess(prescription -> log.info("Found prescription {} with appointment id {}",
                                                               prescription,
                                                               appointmentId));
    }

    @PreAuthorize("hasAnyRole({'client_admin', 'client_doctor'})")
    public Mono<Prescription> save(Prescription prescription) {
        return repository.save(prescription);
    }

    @PreAuthorize("hasAnyRole({'client_admin', 'client_doctor'})")
    public Mono<Prescription> update(Integer id, Prescription prescription) {
        return repository.findById(id)
                         .switchIfEmpty(Mono.error(new IllegalArgumentException("Prescription with id %s not found".formatted(
                                 id))))
                         .flatMap(existingPrescription -> {
                             if (prescription.getAppointmentId() != null) {
                                 existingPrescription.setAppointmentId(prescription.getAppointmentId());
                             }
                             if (prescription.getDescription() != null) {
                                 existingPrescription.setDescription(prescription.getDescription());
                             }
                             return repository.save(existingPrescription);
                         });
    }


}
