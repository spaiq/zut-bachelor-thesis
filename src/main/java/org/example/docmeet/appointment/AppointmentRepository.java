package org.example.docmeet.appointment;

import org.example.docmeet.appointment.enums.AppointmentStateEnum;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface AppointmentRepository extends ReactiveCrudRepository<Appointment, Integer> {
    Flux<Appointment> findByDoctorId(Integer doctorId, Pageable pageable);

    Flux<Appointment> findByPatientId(Integer patientId);

    Flux<Appointment> findByPatientId(Integer patientId, Pageable pageable);

    Flux<Appointment> findByState(AppointmentStateEnum state, Pageable pageable);

    Flux<Appointment> findByDoctorIdAndState(Integer doctorId, AppointmentStateEnum stateEnum, Pageable pageable);
}
