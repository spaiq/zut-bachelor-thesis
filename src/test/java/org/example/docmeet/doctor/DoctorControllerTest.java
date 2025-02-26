package org.example.docmeet.doctor;

import org.example.docmeet.responses.DoctorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorControllerTest {

    @Mock
    private DoctorService service;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DoctorController doctorController;

    @Test
    public void testFindAll() {
        DoctorResponse dr = new DoctorResponse();
        when(service.findAll()).thenReturn(Flux.just(dr));

        StepVerifier.create(doctorController.findAll()).expectNext(dr).verifyComplete();

        verify(service).findAll();
    }

    @Test
    public void testFindById() {
        int id = 1;
        DoctorResponse dr = new DoctorResponse();
        when(service.findById(id)).thenReturn(Mono.just(dr));

        StepVerifier.create(doctorController.findById(id)).expectNext(dr).verifyComplete();

        verify(service).findById(id);
    }

    @Test
    public void testFindDoctorNameById() {
        int id = 1;
        String name = "Dr. Test";
        when(service.findDoctorNameById(id)).thenReturn(Mono.just(name));

        StepVerifier.create(doctorController.findDoctorNameById(id)).expectNext(name).verifyComplete();

        verify(service).findDoctorNameById(id);
    }

    @Test
    public void testFindCurrentUser() {
        String email = "test@example.com";
        DoctorResponse dr = new DoctorResponse();
        when(authentication.getName()).thenReturn(email);
        when(service.findByEmail(email)).thenReturn(Mono.just(dr));

        StepVerifier.create(doctorController.findCurrentUser(authentication)).expectNext(dr).verifyComplete();

        verify(service).findByEmail(email);
    }

    @Test
    public void testFindBySpecialityId() {
        int specialityId = 1;
        int page = 0;
        int size = 10;
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<DoctorResponse> doctorPage = new PageImpl<>(List.of(new DoctorResponse()));
        when(service.findBySpecialityId(specialityId, pageable)).thenReturn(Mono.just(doctorPage));

        StepVerifier.create(doctorController.findBySpecialityId(specialityId, page, size))
                    .expectNext(doctorPage)
                    .verifyComplete();

        verify(service).findBySpecialityId(specialityId, pageable);
    }

    @Test
    public void testSave() {

        Doctor doctor = new Doctor();
        DoctorResponse savedDoctor = new DoctorResponse();
        savedDoctor.setId(1);


        ServerHttpRequest request = mock(ServerHttpRequest.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        HttpHeaders headers = new HttpHeaders();
        URI requestUri = URI.create("http://localhost/api/v1/doctor");

        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(request.getURI()).thenReturn(requestUri);
        when(response.getHeaders()).thenReturn(headers);

        when(service.save(doctor)).thenReturn(Mono.just(savedDoctor));

        StepVerifier.create(doctorController.save(doctor, exchange)).expectNext(savedDoctor).verifyComplete();

        verify(service).save(doctor);


        URI expectedLocation = requestUri.resolve(requestUri.getPath() + "/" + savedDoctor.getId());
        assertEquals(expectedLocation, headers.getLocation());

        verify(response).setStatusCode(HttpStatus.CREATED);
    }

    @Test
    public void testUpdate() {
        int id = 1;
        Doctor doctor = new Doctor();
        DoctorResponse updatedDoctor = new DoctorResponse();
        when(service.update(id, doctor)).thenReturn(Mono.just(updatedDoctor));

        StepVerifier.create(doctorController.update(id, doctor)).expectNext(updatedDoctor).verifyComplete();

        verify(service).update(id, doctor);
    }

    @Test
    public void testDeleteById() {
        int id = 1;
        when(service.deleteById(id)).thenReturn(Mono.empty());

        StepVerifier.create(doctorController.deleteById(id)).verifyComplete();

        verify(service).deleteById(id);
    }
}
