package org.example.docmeet.user;

import lombok.RequiredArgsConstructor;
import org.example.docmeet.authorization.HasAnyRole;
import org.example.docmeet.authorization.IsAdmin;
import org.example.docmeet.authorization.RequireOwnership;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    @IsAdmin
    public Flux<User> findAll() {
        return repository.findAll();
    }

    @IsAdmin
    public Mono<User> findById(UUID id) {
        return repository.findById(id);
    }

    @RequireOwnership
    public Mono<User> create(User user) {
        return repository.create(user);
    }

    @HasAnyRole("{client_admin, client_user}")
    @RequireOwnership
    public Mono<User> save(User user) {
        return repository.save(user);
    }

    @IsAdmin
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }
}
