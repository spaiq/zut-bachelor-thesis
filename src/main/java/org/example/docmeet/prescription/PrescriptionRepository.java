package org.example.docmeet.prescription;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PrescriptionRepository extends ReactiveCrudRepository<Prescription, Integer> {
    Flux<Prescription> findAllById(Integer id);

    Mono<Prescription> findByAppointmentId(Integer appointmentId);
}
