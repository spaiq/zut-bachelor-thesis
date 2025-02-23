package org.example.docmeet.doctor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DoctorRepository extends ReactiveCrudRepository<Doctor, Integer> {
    Flux<Doctor> findBySpecialityId(Integer specialityId, Pageable pageable);

    Flux<Doctor> findBySpecialityId(Integer specialityId);

    Mono<Doctor> findByUserId(Integer userId);
}
