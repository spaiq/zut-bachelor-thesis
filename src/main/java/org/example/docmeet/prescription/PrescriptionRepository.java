package org.example.docmeet.prescription;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PrescriptionRepository extends ReactiveCrudRepository<Prescription, Integer> {
}
