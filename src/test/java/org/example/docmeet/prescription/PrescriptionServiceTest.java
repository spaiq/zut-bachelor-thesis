package org.example.docmeet.prescription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PrescriptionServiceTest {

    @Mock
    private PrescriptionRepository repository;

    @InjectMocks
    private PrescriptionService service;

    private Prescription dummyPrescription() {
        return Prescription.builder().id(1).appointmentId(10).description("Initial description").build();
    }

    @Test
    public void testFindAll() {
        Prescription p = dummyPrescription();
        when(repository.findAll()).thenReturn(Flux.just(p));
        StepVerifier.create(service.findAll()).expectNext(p).verifyComplete();
        verify(repository).findAll();
    }

    @Test
    public void testFindById() {
        Prescription p = dummyPrescription();
        when(repository.findById(1)).thenReturn(Mono.just(p));
        StepVerifier.create(service.findById(1)).expectNext(p).verifyComplete();
        verify(repository).findById(1);
    }

    @Test
    public void testFindByAppointmentId() {
        Prescription p = dummyPrescription();
        when(repository.findByAppointmentId(10)).thenReturn(Mono.just(p));
        StepVerifier.create(service.findByAppointmentId(10)).expectNext(p).verifyComplete();
        verify(repository).findByAppointmentId(10);
    }

    @Test
    public void testSave() {
        Prescription p = dummyPrescription();
        when(repository.save(p)).thenReturn(Mono.just(p));
        StepVerifier.create(service.save(p)).expectNext(p).verifyComplete();
        verify(repository).save(p);
    }

    @Test
    public void testUpdate_Success() {
        Prescription existing = dummyPrescription();
        when(repository.findById(1)).thenReturn(Mono.just(existing));
        Prescription updateData = Prescription.builder().id(20).description("Updated description").build();
        Prescription updated = dummyPrescription();
        updated.setAppointmentId(20);
        updated.setDescription("Updated description");
        when(repository.save(any(Prescription.class))).thenReturn(Mono.just(updated));
        StepVerifier.create(service.update(1, updateData)).assertNext(p -> {
            assert p.getAppointmentId() == 20;
            assert "Updated description".equals(p.getDescription());
        }).verifyComplete();
        verify(repository).findById(1);
        verify(repository).save(any(Prescription.class));
    }

    @Test
    public void testUpdate_NotFound() {
        when(repository.findById(1)).thenReturn(Mono.empty());
        Prescription updateData = Prescription.builder().id(20).description("Updated description").build();

        StepVerifier.create(service.update(1, updateData))
                    .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                                                     throwable.getMessage().equals("Prescription with id 1 not found"))
                    .verify();
        verify(repository).findById(1);
    }
}
