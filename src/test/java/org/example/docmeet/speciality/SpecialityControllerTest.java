package org.example.docmeet.speciality;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SpecialityControllerTest {

    @Mock
    private SpecialityService service;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @InjectMocks
    private SpecialityController specialityController;

    private Speciality dummySpeciality() {
        return Speciality.builder().id(1).name("Cardiology").build();
    }

    @Test
    public void testFindAll() {
        Speciality speciality = dummySpeciality();
        when(service.findAll()).thenReturn(Flux.just(speciality));
        StepVerifier.create(specialityController.findAll()).expectNext(speciality).verifyComplete();
        verify(service).findAll();
    }

    @Test
    public void testSave_Success() {
        Speciality speciality = dummySpeciality();
        when(service.save(speciality)).thenReturn(Mono.just(speciality));
        when(exchange.getRequest()).thenReturn(request);
        URI baseUri = URI.create("http://localhost/api/v1/speciality");
        when(request.getURI()).thenReturn(baseUri);
        when(exchange.getResponse()).thenReturn(response);
        HttpHeaders headers = new HttpHeaders();
        when(response.getHeaders()).thenReturn(headers);
        StepVerifier.create(specialityController.save(speciality, exchange)).expectNext(speciality).verifyComplete();
        verify(response).setStatusCode(HttpStatus.CREATED);
        URI expectedLocation = baseUri.resolve(baseUri.getPath() + "/" + speciality.getId());
        assertEquals(expectedLocation, headers.getLocation());
    }

    @Test
    public void testSave_DuplicateKey() {
        Speciality speciality = dummySpeciality();
        DuplicateKeyException dke = new DuplicateKeyException("duplicate");
        when(service.save(speciality)).thenReturn(Mono.error(dke));
        StepVerifier.create(specialityController.save(speciality, exchange))
                    .expectErrorMatches(throwable -> throwable instanceof DataIntegrityViolationException &&
                                                     throwable.getMessage()
                                                              .contains("Speciality with name " + speciality.getName() +
                                                                        " already exists"))
                    .verify();
    }
}
