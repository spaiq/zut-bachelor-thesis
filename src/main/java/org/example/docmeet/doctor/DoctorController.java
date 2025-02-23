package org.example.docmeet.doctor;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.docmeet.responses.DoctorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@SecurityRequirement(name = "Keycloak")
@Slf4j
public class DoctorController {

    private final DoctorService service;

    @GetMapping("/doctors")
    public Flux<DoctorResponse> findAll() {
        return service.findAll().doOnError(e -> log.error("Error while finding all doctors", e));
    }

    @GetMapping("/doctor/{id}")
    public Mono<DoctorResponse> findById(@PathVariable Integer id) {
        return service.findById(id);
    }

    @GetMapping("/doctor/{id}/name")
    public Mono<String> findDoctorNameById(@PathVariable Integer id) {
        return service.findDoctorNameById(id);
    }

    @PreAuthorize("hasAnyRole({'client_doctor', 'client_user'})")
    @GetMapping("/doctor/current")
    public Mono<DoctorResponse> findCurrentUser(Authentication authentication) {
        return service.findByEmail(authentication.getName());
    }

    @GetMapping("/doctors/speciality/{specialityId}")
    public Mono<Page<DoctorResponse>> findBySpecialityId(@PathVariable Integer specialityId,
                                                         @RequestParam(defaultValue = "0") Integer page,
                                                         @RequestParam(defaultValue = "10") Integer size) {
        return service.findBySpecialityId(specialityId, Pageable.ofSize(size).withPage(page))
                      .doOnError(e -> log.error("Error while finding doctors with speciality id {}", specialityId, e));
    }

    @PostMapping("/doctor")
    public Mono<DoctorResponse> save(@RequestBody Doctor doctor, ServerWebExchange exchange) {
        return service.save(doctor)
                      .doOnSuccess(doc -> {
                          exchange.getResponse().setStatusCode(HttpStatus.CREATED);
                          URI location = exchange.getRequest().getURI();
                          exchange.getResponse()
                                  .getHeaders()
                                  .setLocation(location.resolve(location.getPath() + "/" + doc.getId()));
                          log.info("Doctor {} saved", doc);
                      })
                      .doOnError(e -> log.error("Error while saving doctor {}", doctor, e))
                      .onErrorResume(DuplicateKeyException.class,
                                     e -> Mono.error(new DataIntegrityViolationException("Error while saving doctor",
                                                                                         e)));
    }

    @PatchMapping("/doctor/{id}")
    public Mono<DoctorResponse> update(@PathVariable Integer id,
                                       @RequestBody Doctor doctor,
                                       ServerWebExchange exchange) {
        return service.update(id, doctor, exchange);

    }

    @DeleteMapping("/doctor/{id}")
    public Mono<Void> deleteById(@PathVariable Integer id) {
        return service.deleteById(id);
    }
}
