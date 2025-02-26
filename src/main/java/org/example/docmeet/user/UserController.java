package org.example.docmeet.user;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.docmeet.authorization.IsAdmin;
import org.example.docmeet.validation.CreateValidation;
import org.example.docmeet.validation.UpdateValidation;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@SecurityRequirement(name = "Keycloak")
@Slf4j
public class UserController {

    private final UserService service;

    @GetMapping("/users")
    public Flux<User> findAll() {
        return service.findAll().doOnError(e -> log.error("Error while finding all users", e));
    }

    @GetMapping("/user/{id}")
    public Mono<User> findById(@PathVariable Integer id) {
        return service.findById(id)
                      .switchIfEmpty(Mono.error(new NoResourceFoundException("User with id %s not found".formatted(id))))
                      .doOnError(e -> log.error("Error while finding user with id {}", id, e))
                      .doOnSuccess(User::logUserFound);
    }

    @IsAdmin
    @GetMapping("/user/email/{email}")
    public Mono<User> findByEmail(@PathVariable String email) {
        return service.findByEmail(email)
                      .doOnError(e -> log.error("Error while finding user with email {}", email, e))
                      .doOnSuccess(User::logUserFound);
    }

    @PreAuthorize("hasAnyRole({'client_doctor', 'client_user'})")
    @GetMapping("/user/current")
    public Mono<User> findCurrentUser(Authentication authentication) {
        return service.findByEmail(authentication.getName());
    }

    @PostMapping("/user")
    Mono<User> save(@Validated(CreateValidation.class) @RequestBody User user,
                    ServerWebExchange exchange,
                    Authentication authentication) {
        return Mono.just(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_client_admin")) ||
                         user.getEmail().equals(authentication.getName()))
                   .flatMap(isPermitted -> isPermitted ? service.save(user) :
                           Mono.error(new AccessDeniedException("Access to user %s denied for %s".formatted(user,
                                                                                                            authentication.getName()))))
                   .doOnSuccess(o -> log.info("Granted access to user {} for {}", user, authentication.getName()))
                   .doOnSuccess(savedUser -> {
                       exchange.getResponse().setStatusCode(HttpStatus.CREATED);
                       URI location = exchange.getRequest().getURI();
                       exchange.getResponse()
                               .getHeaders()
                               .setLocation(location.resolve(location.getPath() + "/" + savedUser.getId()));
                       log.info("User with id {} saved", savedUser.getId());
                   })
                   .doOnError(e -> log.error("Error while saving user {}", user, e))
                   .onErrorResume(DuplicateKeyException.class,
                                  e -> Mono.error(new DataIntegrityViolationException("Cannot save user - duplicate key",
                                                                                      e)));
    }

    @PatchMapping("/user/{id}")
    @IsAdmin
    public Mono<Void> update(@RequestBody User user, @PathVariable Integer id, ServerWebExchange exchange) {
        return service.update(id, user, exchange).doOnSuccess(User::logUserUpdated)
                .then();
    }

    @PreAuthorize("hasAnyRole({'client_doctor', 'client_user'})")
    @PatchMapping("/user/current")
    public Mono<User> updateCurrentUser(@Validated(UpdateValidation.class) @RequestBody User user,
                                        ServerWebExchange exchange,
                                        Authentication authentication) {
        return service.findByEmail(authentication.getName())
                      .flatMap(currentUser -> service.update(currentUser.getId(), user, exchange))
                      .doOnSuccess(User::logUserUpdated);
    }

    @DeleteMapping("/user/{id}")
    public Mono<Void> deleteById(@PathVariable Integer id) {
        return service.deleteById(id);
    }
}
