package org.example.docmeet.user;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends ReactiveCrudRepository<User, UUID> {

    @Query("INSERT INTO users (id, email, name, second_name, surname, pesel, phone_number) VALUES (:id, :email, :name, :secondName, :surname, :pesel, :phoneNumber)")
    Mono<User> create(User user);
}
