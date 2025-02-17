package org.example.docmeet.speciality;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.relational.core.sql.LockMode;
import org.springframework.data.relational.repository.Lock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/specialities")
@RequiredArgsConstructor
public class SpecialityController {

    private final SpecialityService service;

    @GetMapping
    @PreAuthorize("hasRole('client_user')")
    Flux<Speciality> findAll() {
        return service.findAll();
    }

    @PostMapping
    Mono<ResponseEntity<Speciality>> save(@Valid @RequestBody Speciality speciality) {
        return service.save(speciality)
                .map(user -> ResponseEntity.status(HttpStatus.CREATED).body(user)).
                onErrorResume(DuplicateKeyException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build()));
    }
}
