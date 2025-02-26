package org.example.docmeet.doctor;

import org.example.docmeet.speciality.Speciality;
import org.example.docmeet.user.User;
import org.example.docmeet.user.UserService;
import org.example.docmeet.user.UserRepository;
import org.example.docmeet.speciality.SpecialityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private DoctorRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SpecialityRepository specialityRepository;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor dummyDoctor() {
        Doctor doctor = new Doctor();
        doctor.setId(1);
        doctor.setUserId(100);
        doctor.setSpecialityId(200);
        return doctor;
    }

    private User dummyUser() {
        return User.builder().id(100).name("John").secondName("Adam").surname("Doe").build();
    }

    private Speciality dummySpeciality() {
        return Speciality.builder().id(100).name("Cardiology").build();
    }

    @Test
    public void testFindAll() {
        Doctor doctor = dummyDoctor();
        when(repository.findAll()).thenReturn(Flux.just(doctor));
        when(userRepository.findById(doctor.getUserId())).thenReturn(Mono.just(dummyUser()));
        when(specialityRepository.findById(doctor.getSpecialityId())).thenReturn(Mono.just(dummySpeciality()));
        StepVerifier.create(doctorService.findAll())
                    .expectNextMatches(dr -> dr.getId().equals(doctor.getId()))
                    .verifyComplete();
    }

    @Test
    public void testFindById() {
        Doctor doctor = dummyDoctor();
        when(repository.findById(doctor.getId())).thenReturn(Mono.just(doctor));
        when(userRepository.findById(doctor.getUserId())).thenReturn(Mono.just(dummyUser()));
        when(specialityRepository.findById(doctor.getSpecialityId())).thenReturn(Mono.just(dummySpeciality()));
        StepVerifier.create(doctorService.findById(doctor.getId()))
                    .expectNextMatches(dr -> dr.getId().equals(doctor.getId()))
                    .verifyComplete();
    }

    @Test
    public void testFindByEmail() {
        String email = "test@example.com";
        User user = dummyUser();
        Doctor doctor = dummyDoctor();
        when(userService.findByEmail(email)).thenReturn(Mono.just(user));
        when(repository.findByUserId(user.getId())).thenReturn(Mono.just(doctor));
        when(userRepository.findById(doctor.getUserId())).thenReturn(Mono.just(user));
        when(specialityRepository.findById(doctor.getSpecialityId())).thenReturn(Mono.just(dummySpeciality()));
        StepVerifier.create(doctorService.findByEmail(email))
                    .expectNextMatches(dr -> dr.getId().equals(doctor.getId()))
                    .verifyComplete();
    }

    @Test
    public void testFindBySpecialityId() {
        int specialityId = 200;
        Pageable pageable = PageRequest.of(0, 10);
        Doctor doctor = dummyDoctor();
        when(repository.findBySpecialityId(specialityId, pageable)).thenReturn(Flux.just(doctor));
        when(userRepository.findById(doctor.getUserId())).thenReturn(Mono.just(dummyUser()));
        when(specialityRepository.findById(doctor.getSpecialityId())).thenReturn(Mono.just(dummySpeciality()));
        StepVerifier.create(doctorService.findBySpecialityId(specialityId, pageable)).assertNext(page -> {
            assertEquals(1, page.getTotalElements());
            assertEquals(doctor.getId(), page.getContent().getFirst().getId());
        }).verifyComplete();
    }

    @Test
    public void testFindDoctorNameById() {
        Doctor doctor = dummyDoctor();
        User user = dummyUser();
        when(repository.findById(doctor.getId())).thenReturn(Mono.just(doctor));
        when(userRepository.findById(doctor.getUserId())).thenReturn(Mono.just(user));
        String expectedName = org.example.docmeet.utils.Utils.getFullName(user, doctor);
        StepVerifier.create(doctorService.findDoctorNameById(doctor.getId())).expectNext(expectedName).verifyComplete();
    }

    @Test
    public void testSave() {
        Doctor doctor = dummyDoctor();
        when(repository.save(doctor)).thenReturn(Mono.just(doctor));
        when(userRepository.findById(doctor.getUserId())).thenReturn(Mono.just(dummyUser()));
        when(specialityRepository.findById(doctor.getSpecialityId())).thenReturn(Mono.just(dummySpeciality()));
        StepVerifier.create(doctorService.save(doctor))
                    .expectNextMatches(dr -> dr.getId().equals(doctor.getId()))
                    .verifyComplete();
    }

    @Test
    public void testUpdate() {
        int id = 1;
        Doctor existing = dummyDoctor();
        Doctor updatedInput = new Doctor();
        updatedInput.setSpecialityId(300);
        existing.setSpecialityId(200);
        Doctor updatedDoctor = dummyDoctor();
        updatedDoctor.setSpecialityId(300);

        when(repository.findById(id)).thenReturn(Mono.just(existing));
        when(repository.save(any(Doctor.class))).thenReturn(Mono.just(updatedDoctor));
        when(userRepository.findById(updatedDoctor.getUserId())).thenReturn(Mono.just(dummyUser()));
        when(specialityRepository.findById(updatedDoctor.getSpecialityId())).thenReturn(Mono.just(dummySpeciality()));

        StepVerifier.create(doctorService.update(id, updatedInput))
                    .expectNextMatches(dr -> dr.getId().equals(updatedDoctor.getId()) &&
                                             updatedDoctor.getSpecialityId().equals(300))
                    .verifyComplete();
    }

    @Test
    public void testDeleteById_Success() {
        int id = 1;
        when(repository.existsById(id)).thenReturn(Mono.just(true));
        when(repository.deleteById(id)).thenReturn(Mono.empty());
        StepVerifier.create(doctorService.deleteById(id)).verifyComplete();
    }

    @Test
    public void testDeleteById_NotFound() {
        int id = 1;
        when(repository.existsById(id)).thenReturn(Mono.just(false));
        StepVerifier.create(doctorService.deleteById(id)).expectError(NoResourceFoundException.class).verify();
    }

    @Test
    public void testConvertToMono() {
        Doctor doctor = dummyDoctor();
        when(userRepository.findById(doctor.getUserId())).thenReturn(Mono.just(dummyUser()));
        when(specialityRepository.findById(doctor.getSpecialityId())).thenReturn(Mono.just(dummySpeciality()));
        StepVerifier.create(doctorService.convertToMono(doctor))
                    .expectNextMatches(dr -> dr.getId().equals(doctor.getId()))
                    .verifyComplete();
    }

    @Test
    public void testConvertToFlux() {
        Doctor doctor = dummyDoctor();
        when(userRepository.findById(doctor.getUserId())).thenReturn(Mono.just(dummyUser()));
        when(specialityRepository.findById(doctor.getSpecialityId())).thenReturn(Mono.just(dummySpeciality()));
        StepVerifier.create(doctorService.convertToFlux(doctor))
                    .expectNextMatches(dr -> dr.getId().equals(doctor.getId()))
                    .verifyComplete();
    }
}
