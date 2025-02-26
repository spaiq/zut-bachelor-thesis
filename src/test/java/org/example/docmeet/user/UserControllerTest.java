package org.example.docmeet.user;

import org.example.docmeet.user.User;
import org.example.docmeet.user.UserController;
import org.example.docmeet.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService service;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    private User dummyUser() {
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        return user;
    }

    @Test
    public void testFindAll() {
        User user = dummyUser();
        when(service.findAll()).thenReturn(Flux.just(user));
        StepVerifier.create(userController.findAll()).expectNext(user).verifyComplete();
        verify(service).findAll();
    }

    @Test
    public void testFindById() {
        User user = dummyUser();
        when(service.findById(1)).thenReturn(Mono.just(user));
        StepVerifier.create(userController.findById(1)).expectNext(user).verifyComplete();
        verify(service).findById(1);
    }

    @Test
    public void testFindByEmail() {
        User user = dummyUser();
        when(service.findByEmail("test@example.com")).thenReturn(Mono.just(user));
        StepVerifier.create(userController.findByEmail("test@example.com")).expectNext(user).verifyComplete();
        verify(service).findByEmail("test@example.com");
    }

    @Test
    public void testFindCurrentUser() {
        User user = dummyUser();
        when(authentication.getName()).thenReturn("test@example.com");
        when(service.findByEmail("test@example.com")).thenReturn(Mono.just(user));
        StepVerifier.create(userController.findCurrentUser(authentication)).expectNext(user).verifyComplete();
        verify(service).findByEmail("test@example.com");
    }

    @Test
    public void testSave_Permitted() {
        User user = dummyUser();
        user.setId(1);
        when(authentication.getName()).thenReturn("test@example.com");
        when(authentication.getAuthorities()).thenReturn(java.util.Collections.emptyList());
        org.springframework.http.server.reactive.ServerHttpRequest request = mock(org.springframework.http.server.reactive.ServerHttpRequest.class);
        org.springframework.http.server.reactive.ServerHttpResponse response = mock(org.springframework.http.server.reactive.ServerHttpResponse.class);
        HttpHeaders headers = new HttpHeaders();
        URI uri = URI.create("http://localhost/api/v1/user");
        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(request.getURI()).thenReturn(uri);
        when(response.getHeaders()).thenReturn(headers);
        when(service.save(user)).thenReturn(Mono.just(user));
        StepVerifier.create(userController.save(user, exchange, authentication)).expectNext(user).verifyComplete();
        verify(service).save(user);
        URI expectedLocation = uri.resolve(uri.getPath() + "/" + user.getId());
        assertEquals(expectedLocation, headers.getLocation());
        verify(response).setStatusCode(HttpStatus.CREATED);
    }

    @Test
    public void testSave_AccessDenied() {
        User user = dummyUser();
        when(authentication.getName()).thenReturn("other@example.com");
        when(authentication.getAuthorities()).thenReturn(java.util.Collections.emptyList());
        StepVerifier.create(userController.save(user, exchange, authentication))
                    .expectError(AccessDeniedException.class)
                    .verify();
    }

    @Test
    public void testSave_DuplicateKey() {
        User user = dummyUser();
        when(authentication.getName()).thenReturn("test@example.com");
        when(authentication.getAuthorities()).thenReturn(java.util.Collections.emptyList());
        when(service.save(user)).thenReturn(Mono.error(new DuplicateKeyException("duplicate")));
        org.springframework.http.server.reactive.ServerHttpRequest request = mock(org.springframework.http.server.reactive.ServerHttpRequest.class);
        org.springframework.http.server.reactive.ServerHttpResponse response = mock(org.springframework.http.server.reactive.ServerHttpResponse.class);
        HttpHeaders headers = new HttpHeaders();
        StepVerifier.create(userController.save(user, exchange, authentication))
                    .expectError(DataIntegrityViolationException.class)
                    .verify();
    }

    @Test
    public void testUpdate() {
        User user = dummyUser();
        when(service.update(1, user, exchange)).thenReturn(Mono.just(user));
        StepVerifier.create(userController.update(user, 1, exchange)).verifyComplete();
        verify(service).update(1, user, exchange);
    }

    @Test
    public void testUpdateCurrentUser() {
        User currentUser = dummyUser();
        User updatedUser = dummyUser();
        updatedUser.setEmail("updated@example.com");
        when(authentication.getName()).thenReturn("test@example.com");
        when(service.findByEmail("test@example.com")).thenReturn(Mono.just(currentUser));
        when(service.update(currentUser.getId(), updatedUser, exchange)).thenReturn(Mono.just(updatedUser));
        StepVerifier.create(userController.updateCurrentUser(updatedUser, exchange, authentication))
                    .expectNext(updatedUser)
                    .verifyComplete();
        verify(service).findByEmail("test@example.com");
        verify(service).update(currentUser.getId(), updatedUser, exchange);
    }

    @Test
    public void testDeleteById() {
        when(service.deleteById(1)).thenReturn(Mono.empty());
        StepVerifier.create(userController.deleteById(1)).verifyComplete();
        verify(service).deleteById(1);
    }
}
