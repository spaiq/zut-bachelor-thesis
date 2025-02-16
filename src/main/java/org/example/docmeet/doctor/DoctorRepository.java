package org.example.docmeet.doctor;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface DoctorRepository extends ReactiveCrudRepository<Doctor, Integer> {
}
