package org.example.docmeet.doctor;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface DoctorRepository extends ReactiveCrudRepository<Doctor, UUID> {
}
