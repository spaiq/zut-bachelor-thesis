package org.example.docmeet.appointment;

import org.example.docmeet.appointment.Appointment;
import org.example.docmeet.appointment.AppointmentController;
import org.example.docmeet.appointment.AppointmentService;
import org.example.docmeet.doctor.DoctorService;
import org.example.docmeet.responses.DoctorResponse;
import org.example.docmeet.user.User;
import org.example.docmeet.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class AppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private DoctorService doctorService;

    @Mock
    private UserService userService;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AppointmentController appointmentController;

    private AutoCloseable mocks;

    @BeforeEach
    public void setUp() {
        this.mocks = MockitoAnnotations.openMocks(this);

        Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_client_admin"));

        when(authentication.getName()).thenReturn("test@example.com");
        doReturn(authorities).when(authentication).getAuthorities();
    }

    @AfterEach
    public void tearDown() throws Exception {

        mocks.close();
    }

    @Test
    public void testFindAll() {
        when(appointmentService.findAll()).thenReturn(Flux.just(new Appointment()));

        StepVerifier.create(appointmentController.findAll()).expectNextCount(1).verifyComplete();

        verify(appointmentService, times(1)).findAll();
    }

    @Test
    public void testFindByDoctorId() {
        Page<Appointment> page = new PageImpl<>(List.of(new Appointment()));
        when(appointmentService.findByDoctorId(anyInt(), any(Pageable.class))).thenReturn(Mono.just(page));

        StepVerifier.create(appointmentController.findByDoctorId(1, 0, 10)).expectNext(page).verifyComplete();

        verify(appointmentService, times(1)).findByDoctorId(anyInt(), any(Pageable.class));
    }

    @Test
    public void testFindByPatientId() {
        Page<Appointment> page = new PageImpl<>(List.of(new Appointment()));
        when(appointmentService.findByPatientId(anyInt(), any(Pageable.class))).thenReturn(Mono.just(page));

        StepVerifier.create(appointmentController.findByPatientId(1, 0, 10)).expectNext(page).verifyComplete();

        verify(appointmentService, times(1)).findByPatientId(anyInt(), any(Pageable.class));
    }

    @Test
    public void testFindAvailableBySpecialityId() {
        Page<Appointment> page = new PageImpl<>(List.of(new Appointment()));
        when(appointmentService.findAvailableBySpecialityId(anyInt(),
                                                            any(Pageable.class),
                                                            any(LocalDateTime.class),
                                                            any(LocalDateTime.class))).thenReturn(Mono.just(page));

        LocalDateTime dateTime = LocalDateTime.of(2000, 1, 1, 1, 1);

        StepVerifier.create(appointmentController.findAvailableBySpecialityId(1, 0, 10, dateTime, dateTime))
                    .expectNext(page)
                    .verifyComplete();

        verify(appointmentService, times(1)).findAvailableBySpecialityId(anyInt(),
                                                                         any(Pageable.class),
                                                                         any(LocalDateTime.class),
                                                                         any(LocalDateTime.class));
    }

    @Test
    public void testFindById() {
        Appointment appointment = new Appointment();
        when(appointmentService.findById(anyInt())).thenReturn(Mono.just(appointment));

        StepVerifier.create(appointmentController.findById(1)).expectNext(appointment).verifyComplete();

        verify(appointmentService, times(1)).findById(anyInt());
    }

    @Test
    public void testSave() {
        Appointment appointment = new Appointment();
        appointment.setId(123);
        appointment.setDoctorId(123);

        URI uri = URI.create("http://localhost:8080/api/v1/appointment/123");
        HttpHeaders headers = new HttpHeaders();


        when(request.getURI()).thenReturn(uri);
        when(response.getHeaders()).thenReturn(headers);

        when(doctorService.findById(any(Integer.class))).thenReturn(Mono.just(new DoctorResponse()));
        when(appointmentService.save(any(Appointment.class))).thenReturn(Mono.just(appointment));
        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);

        StepVerifier.create(appointmentController.save(appointment, exchange, authentication))
                    .expectNext(appointment)
                    .verifyComplete();

        verify(appointmentService, times(1)).save(any(Appointment.class));
    }

    @Test
    public void testUpdate() {
        Appointment appointment = new Appointment();
        when(appointmentService.update(anyInt(), any(Appointment.class))).thenReturn(Mono.just(appointment));

        StepVerifier.create(appointmentController.update(1, appointment, authentication))
                    .expectNext(appointment)
                    .verifyComplete();

        verify(appointmentService, times(1)).update(anyInt(), any(Appointment.class));
    }

    @Test
    public void testDeleteById() {
        when(appointmentService.deleteById(anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(appointmentController.deleteById(1)).verifyComplete();

        verify(appointmentService, times(1)).deleteById(anyInt());
    }

    @Test
    public void testBook() {
        User mockUser = new User();
        mockUser.setId(123);

        Appointment appointment = new Appointment();
        when(authentication.getName()).thenReturn("test@example.com");
        when(userService.findByEmail(anyString())).thenReturn(Mono.just(mockUser));
        when(appointmentService.update(anyInt(), any(Appointment.class))).thenReturn(Mono.just(appointment));

        StepVerifier.create(appointmentController.book(1, authentication)).verifyComplete();

        verify(appointmentService, times(1)).update(anyInt(), any(Appointment.class));
    }

    @Test
    public void testCancelUser() {
        Appointment appointment = new Appointment();
        when(userService.findByEmail(anyString())).thenReturn(Mono.just(User.builder().id(123).build()));
        when(appointmentService.findByPatientId(anyInt())).thenReturn(Flux.just(appointment));
        when(appointmentService.update(anyInt(), any(Appointment.class))).thenReturn(Mono.just(appointment));
        when(appointmentService.deletePatientIdFromAppointment(anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(appointmentController.cancelUser(1, authentication)).verifyComplete();

        verify(appointmentService, times(1)).update(anyInt(), any(Appointment.class));
        verify(appointmentService, times(1)).deletePatientIdFromAppointment(anyInt());
    }
}