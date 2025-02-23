package org.example.docmeet.speciality;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.docmeet.validation.CreateValidation;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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
public class SpecialityController {

    private final SpecialityService service;

    @GetMapping("/specialities")
    Flux<Speciality> findAll() {
        return service.findAll();
    }

    @PostMapping("/speciality")
    Mono<Speciality> save(@Validated(CreateValidation.class) @RequestBody Speciality speciality,
                          ServerWebExchange exchange) {
        return service.save(speciality)
                      .doOnSuccess(spec -> {
                          exchange.getResponse().setStatusCode(HttpStatus.CREATED);
                          URI location = exchange.getRequest().getURI();
                          exchange.getResponse()
                                  .getHeaders()
                                  .setLocation(location.resolve(location.getPath() + "/" + spec.getId()));
                          log.info("Speciality {} saved", spec);
                      })
                      .doOnError(e -> log.error("Error while saving speciality {}", speciality, e))
                      .onErrorResume(DuplicateKeyException.class,
                                     e -> Mono.error(new DataIntegrityViolationException(
                                             "Speciality with name %s already exists".formatted(speciality.getName()),
                                             e)));
    }
}
