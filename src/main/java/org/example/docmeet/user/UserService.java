package org.example.docmeet.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.docmeet.authorization.IsAdmin;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository repository;

    @IsAdmin
    public Flux<User> findAll() {
        return repository.findAll();
    }

    @IsAdmin
    public Mono<User> findById(Integer id) {
        return repository.findById(id);
    }

    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email)
                         .doOnError(e -> log.error("Error while finding current user {}", email, e))
                         .doOnSuccess(User::logUserFound);
    }

    @PreAuthorize("hasAnyRole({'client_admin', 'client_user'})")
    public Mono<User> save(User user) {
        return repository.save(user);
    }

    @PreAuthorize("hasAnyRole({'client_admin', 'client_user'})")
    public Mono<User> update(Integer id, User user, ServerWebExchange exchange) {
        return repository.findById(id)
                         .switchIfEmpty(Mono.error(new NoResourceFoundException(exchange.getRequest()
                                                                                        .getPath()
                                                                                        .toString())))
                         .doOnError(e -> log.error("Error while updating user with id {}", id, e))
                         .doOnSuccess(User::logUserFound)
                         .flatMap(existingUser -> {
                             if (user.getEmail() != null) {
                                 existingUser.setEmail(user.getEmail());
                             }
                             if (user.getName() != null) {
                                 existingUser.setName(user.getName());
                             }
                             if (user.getSecondName() != null) {
                                 existingUser.setSecondName(user.getSecondName());
                             }
                             if (user.getSurname() != null) {
                                 existingUser.setSurname(user.getSurname());
                             }
                             if (user.getPesel() != null) {
                                 existingUser.setPesel(user.getPesel());
                             }
                             if (user.getPhoneNumber() != null) {
                                 existingUser.setPhoneNumber(user.getPhoneNumber());
                             }
                             return repository.save(existingUser);
                         });

    }

    @IsAdmin
    public Mono<Void> deleteById(Integer id) {
        return repository.existsById(id)
                         .flatMap(bool -> bool ? Mono.empty() :
                                 Mono.error(new NoResourceFoundException("Cannot delete. User with id %s does not exist".formatted(
                                         id))))
                         .doOnError(e -> log.error("Error while deleting user with id {}", id, e))
                         .switchIfEmpty(repository.deleteById(id))
                         .doOnSuccess(o -> log.info("User with id {} deleted", id))
                .then();
    }
}
