package org.example.docmeet.prescription;

import org.example.docmeet.appointment.Appointment;
import org.example.docmeet.appointment.AppointmentService;
import org.example.docmeet.user.User;
import org.example.docmeet.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PrescriptionControllerTest {

    @Mock
    private PrescriptionService service;

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PrescriptionController controller;

    private Prescription dummyPrescription() {
        return Prescription.builder().id(10).build();
    }

    private Appointment dummyAppointment() {
        Appointment appointment = new Appointment();
        appointment.setId(100);
        return appointment;
    }

    private User dummyUser() {
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        return user;
    }

    @Test
    public void testFindAll() {
        Prescription prescription = dummyPrescription();
        when(service.findAll()).thenReturn(Flux.just(prescription));
        StepVerifier.create(controller.findAll()).expectNext(prescription).verifyComplete();
        verify(service).findAll();
    }

    @Test
    public void testFindById() {
        Prescription prescription = dummyPrescription();
        when(service.findById(10)).thenReturn(Mono.just(prescription));
        StepVerifier.create(controller.findById(10)).expectNext(prescription).verifyComplete();
        verify(service).findById(10);
    }

    @Test
    public void testFindByAppointmentId() {
        Prescription prescription = dummyPrescription();
        when(service.findByAppointmentId(100)).thenReturn(Mono.just(prescription));
        StepVerifier.create(controller.findByAppointmentId(100)).expectNext(prescription).verifyComplete();
        verify(service).findByAppointmentId(100);
    }

    @Test
    public void testFindCurrentUser() {
        int page = 0;
        int size = 10;
        Prescription prescription = dummyPrescription();
        Appointment appointment = dummyAppointment();
        User user = dummyUser();
        when(authentication.getName()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(Mono.just(user));
        when(appointmentService.findByPatientId(user.getId())).thenReturn(Flux.just(appointment));
        when(service.findByAppointmentId(appointment.getId())).thenReturn(Mono.just(prescription));
        StepVerifier.create(controller.findCurrentUser(page, size, authentication)).assertNext(pageResult -> {
            assertEquals(1, pageResult.getTotalElements());
            assertEquals(prescription.getId(), pageResult.getContent().getFirst().getId());
            Pageable expectedPageable = Pageable.ofSize(size).withPage(page);
            assertEquals(expectedPageable.getPageSize(), pageResult.getPageable().getPageSize());
        }).verifyComplete();
        verify(userService).findByEmail("test@example.com");
        verify(appointmentService).findByPatientId(user.getId());
        verify(service).findByAppointmentId(appointment.getId());
    }

    @Test
    public void testSave() {
        Prescription prescription = dummyPrescription();
        when(service.save(prescription)).thenReturn(Mono.just(prescription));
        StepVerifier.create(controller.save(prescription)).expectNext(prescription).verifyComplete();
        verify(service).save(prescription);
    }

    @Test
    public void testUpdate() {
        Prescription prescription = dummyPrescription();
        when(service.update(anyInt(), any(Prescription.class))).thenReturn(Mono.just(prescription));
        StepVerifier.create(controller.update(10, prescription)).expectNext(prescription).verifyComplete();
        verify(service).update(10, prescription);
    }
}
