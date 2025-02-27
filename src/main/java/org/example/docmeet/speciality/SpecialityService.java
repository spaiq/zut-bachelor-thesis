package org.example.docmeet.speciality;

import lombok.RequiredArgsConstructor;
import org.example.docmeet.authorization.IsAdmin;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SpecialityService {

    private final SpecialityRepository repository;

    public Flux<Speciality> findAll() {
        return repository.findAll();
    }

    @IsAdmin
    public Mono<Speciality> save(Speciality speciality) {
        return repository.save(speciality);
    }

}
