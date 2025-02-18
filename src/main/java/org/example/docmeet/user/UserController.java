package org.example.docmeet.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.docmeet.speciality.Speciality;
import org.example.docmeet.validation.CreateValidation;
import org.example.docmeet.validation.UpdateValidation;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    public Flux<User> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<User> findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    Mono<ResponseEntity<User>> create(@Validated(CreateValidation.class) @RequestBody User user) {
        return service.create(user)
                .map(u -> ResponseEntity.status(HttpStatus.CREATED).body(u)).
                onErrorResume(DuplicateKeyException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build()));
    }

    @PatchMapping
    public Mono<ResponseEntity<Void>> update(@Validated(UpdateValidation.class) @RequestBody User user) {
        return service.save(user).map(u -> ResponseEntity.status(HttpStatus.OK).build());
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteById(@PathVariable UUID id) {
        return service.deleteById(id);
    }
}
