package org.example.docmeet.speciality;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.docmeet.validation.CreateValidation;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.relational.core.sql.LockMode;
import org.springframework.data.relational.repository.Lock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/specialities")
@RequiredArgsConstructor
public class SpecialityController {

    private final SpecialityService service;

    @GetMapping
    Flux<Speciality> findAll() {
        return service.findAll();
    }

    @PostMapping
    Mono<ResponseEntity<Speciality>> save(@Validated(CreateValidation.class) @RequestBody Speciality speciality) {
        return service.save(speciality)
                .map(spec -> ResponseEntity.status(HttpStatus.CREATED).body(spec)).
                onErrorResume(DuplicateKeyException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build()));
    }
}
