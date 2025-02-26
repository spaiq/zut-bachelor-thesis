package org.example.docmeet.speciality;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SpecialityServiceTest {

    @Mock
    private SpecialityRepository repository;

    @InjectMocks
    private SpecialityService specialityService;

    private Speciality dummySpeciality() {
        return Speciality.builder().id(1).name("Cardiology").build();
    }

    @Test
    public void testFindAll() {
        Speciality speciality = dummySpeciality();
        when(repository.findAll()).thenReturn(Flux.just(speciality));
        StepVerifier.create(specialityService.findAll()).expectNext(speciality).verifyComplete();
        verify(repository).findAll();
    }

    @Test
    public void testSave() {
        Speciality speciality = dummySpeciality();
        when(repository.save(speciality)).thenReturn(Mono.just(speciality));
        StepVerifier.create(specialityService.save(speciality)).expectNext(speciality).verifyComplete();
        verify(repository).save(speciality);
    }
}
