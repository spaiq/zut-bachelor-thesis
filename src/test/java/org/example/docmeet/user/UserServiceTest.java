package org.example.docmeet.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService userService;

    private User dummyUser() {
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setName("John");
        user.setSecondName("M");
        user.setSurname("Doe");
        user.setPesel("12345678901");
        user.setPhoneNumber("123456789");
        return user;
    }

    @Test
    public void testFindAll() {
        User user = dummyUser();
        when(repository.findAll()).thenReturn(Flux.just(user));
        StepVerifier.create(userService.findAll()).expectNext(user).verifyComplete();
        verify(repository).findAll();
    }

    @Test
    public void testFindById() {
        User user = dummyUser();
        when(repository.findById(1)).thenReturn(Mono.just(user));
        StepVerifier.create(userService.findById(1)).expectNext(user).verifyComplete();
        verify(repository).findById(1);
    }

    @Test
    public void testFindByEmail() {
        User user = dummyUser();
        when(repository.findByEmail("test@example.com")).thenReturn(Mono.just(user));
        StepVerifier.create(userService.findByEmail("test@example.com")).expectNext(user).verifyComplete();
        verify(repository).findByEmail("test@example.com");
    }

    @Test
    public void testSave() {
        User user = dummyUser();
        when(repository.save(user)).thenReturn(Mono.just(user));
        StepVerifier.create(userService.save(user)).expectNext(user).verifyComplete();
        verify(repository).save(user);
    }

    @Test
    public void testUpdate_Success() {
        User existing = dummyUser();
        User updateData = new User();
        updateData.setEmail("updated@example.com");
        updateData.setName("Jane");
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        org.springframework.http.server.reactive.ServerHttpRequest request = mock(org.springframework.http.server.reactive.ServerHttpRequest.class);
        when(exchange.getRequest()).thenReturn(request);
        RequestPath requestPath = mock(RequestPath.class);
        when(requestPath.toString()).thenReturn("/api/v1/user");
        when(request.getPath()).thenReturn(requestPath);
        when(repository.findById(1)).thenReturn(Mono.just(existing));
        User updated = dummyUser();
        updated.setEmail("updated@example.com");
        updated.setName("Jane");
        when(repository.save(any(User.class))).thenReturn(Mono.just(updated));
        StepVerifier.create(userService.update(1, updateData, exchange)).expectNext(updated).verifyComplete();
        verify(repository).findById(1);
        verify(repository).save(any(User.class));
    }

    @Test
    public void testUpdate_NotFound() {
        User updateData = new User();
        updateData.setEmail("updated@example.com");
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        when(exchange.getRequest()).thenReturn(request);
        RequestPath requestPath = mock(RequestPath.class);
        when(requestPath.toString()).thenReturn("/api/v1/user");
        when(request.getPath()).thenReturn(requestPath);
        when(repository.findById(1)).thenReturn(Mono.empty());
        StepVerifier.create(userService.update(1, updateData, exchange))
                    .expectError(NoResourceFoundException.class)
                    .verify();
        verify(repository).findById(1);
    }


    @Test
    public void testDeleteById_Success() {
        when(repository.existsById(1)).thenReturn(Mono.just(true));
        when(repository.deleteById(1)).thenReturn(Mono.empty());
        StepVerifier.create(userService.deleteById(1)).verifyComplete();
        verify(repository).existsById(1);
        verify(repository).deleteById(1);
    }

    @Test
    public void testDeleteById_NotFound() {
        when(repository.existsById(1)).thenReturn(Mono.just(false));
        StepVerifier.create(userService.deleteById(1)).expectError(NoResourceFoundException.class).verify();
        verify(repository).existsById(1);
    }
}
