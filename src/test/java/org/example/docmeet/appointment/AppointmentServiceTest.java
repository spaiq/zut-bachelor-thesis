package org.example.docmeet.appointment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.example.docmeet.appointment.enums.AppointmentStateEnum;
import org.example.docmeet.doctor.Doctor;
import org.example.docmeet.doctor.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository repository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    public void testFindAll() {
        Appointment appointment = new Appointment();
        when(repository.findAll()).thenReturn(Flux.just(appointment));

        StepVerifier.create(appointmentService.findAll()).expectNext(appointment).verifyComplete();
    }

    @Test
    public void testFindByDoctorId() {
        int doctorId = 1;
        Pageable pageable = PageRequest.of(0, 10);
        Appointment appointment1 = new Appointment();
        Appointment appointment2 = new Appointment();
        when(repository.findByDoctorId(doctorId, pageable)).thenReturn(Flux.just(appointment1, appointment2));

        StepVerifier.create(appointmentService.findByDoctorId(doctorId, pageable)).assertNext(page -> {
            assertEquals(2, page.getTotalElements());
            assertEquals(2, page.getContent().size());
        }).verifyComplete();
    }

    @Test
    public void testFindByPatientId_Flux() {
        int patientId = 1;
        Appointment appointment = new Appointment();
        when(repository.findByPatientId(patientId)).thenReturn(Flux.just(appointment));

        StepVerifier.create(appointmentService.findByPatientId(patientId)).expectNext(appointment).verifyComplete();
    }

    @Test
    public void testFindByPatientId_Pageable() {
        int patientId = 1;
        Pageable pageable = PageRequest.of(0, 10);
        Appointment appointment1 = new Appointment();
        Appointment appointment2 = new Appointment();
        when(repository.findByPatientId(patientId, pageable)).thenReturn(Flux.just(appointment1, appointment2));

        StepVerifier.create(appointmentService.findByPatientId(patientId, pageable)).assertNext(page -> {
            assertEquals(2, page.getTotalElements());
            assertEquals(2, page.getContent().size());
        }).verifyComplete();
    }

    @Test
    public void testFindAvailableBySpecialityId_Valid() {
        int specialityId = 1;
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.plusDays(1);
        LocalDateTime to = from.plusDays(2);

        // Stub doctorRepository to return one doctor with id = 1
        Doctor doctor = new Doctor();
        doctor.setId(1);
        when(doctorRepository.findBySpecialityId(specialityId)).thenReturn(Flux.just(doctor));

        // Prepare a valid appointment (matching doctor, date, and state)
        Appointment validAppointment = new Appointment();
        validAppointment.setDoctorId(1);
        validAppointment.setDateTime(from.plusHours(1));
        validAppointment.setState(AppointmentStateEnum.AVAILABLE);

        // Also include an appointment that should be filtered out (wrong doctor)
        Appointment invalidAppointment = new Appointment();
        invalidAppointment.setDoctorId(2);
        invalidAppointment.setDateTime(from.plusHours(1));
        invalidAppointment.setState(AppointmentStateEnum.AVAILABLE);

        when(repository.findAll()).thenReturn(Flux.just(validAppointment, invalidAppointment));

        StepVerifier.create(appointmentService.findAvailableBySpecialityId(specialityId, pageable, from, to))
                    .assertNext(page -> {
                        // Only the validAppointment should be returned.
                        assertEquals(1, page.getTotalElements());
                        assertEquals(1, page.getContent().size());
                    })
                    .verifyComplete();
    }

    @Test
    public void testFindAvailableBySpecialityId_InvalidFrom() {
        int specialityId = 1;
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusDays(1); // invalid: before today
        LocalDateTime to = now.plusDays(1);

        StepVerifier.create(appointmentService.findAvailableBySpecialityId(specialityId, pageable, from, to))
                    .expectError(IllegalArgumentException.class)
                    .verify();
    }

    @Test
    public void testFindAvailableBySpecialityId_InvalidTo() {
        int specialityId = 1;
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.plusDays(1);
        LocalDateTime to = now; // invalid: to is before from

        StepVerifier.create(appointmentService.findAvailableBySpecialityId(specialityId, pageable, from, to))
                    .expectError(IllegalArgumentException.class)
                    .verify();
    }

    @Test
    public void testFindByState() {
        AppointmentStateEnum state = AppointmentStateEnum.AVAILABLE;
        Pageable pageable = PageRequest.of(0, 10);
        Appointment appointment = new Appointment();
        when(repository.findByState(state, pageable)).thenReturn(Flux.just(appointment));

        StepVerifier.create(appointmentService.findByState(state, pageable)).expectNext(appointment).verifyComplete();
    }

    @Test
    public void testFindByDoctorIdAndStateAvailable() {
        int doctorId = 1;
        Pageable pageable = PageRequest.of(0, 10);
        Appointment appointment = new Appointment();
        when(repository.findByDoctorIdAndState(doctorId,
                                               AppointmentStateEnum.AVAILABLE,
                                               pageable)).thenReturn(Flux.just(appointment));

        StepVerifier.create(appointmentService.findByDoctorIdAndStateAvailable(doctorId, pageable)).assertNext(page -> {
            assertEquals(1, page.getTotalElements());
            assertEquals(1, page.getContent().size());
        }).verifyComplete();
    }

    @Test
    public void testFindById() {
        int appointmentId = 1;
        Appointment appointment = new Appointment();
        when(repository.findById(appointmentId)).thenReturn(Mono.just(appointment));

        StepVerifier.create(appointmentService.findById(appointmentId)).expectNext(appointment).verifyComplete();
    }

    @Test
    public void testSave() {
        Appointment appointment = new Appointment();
        when(repository.save(appointment)).thenReturn(Mono.just(appointment));

        StepVerifier.create(appointmentService.save(appointment)).expectNext(appointment).verifyComplete();
    }

    @Test
    public void testUpdate_Success() {
        int appointmentId = 1;
        Appointment existing = new Appointment();
        existing.setDoctorId(10);
        existing.setPatientId(20);

        Appointment newData = new Appointment();
        newData.setDoctorId(15); // new value
        // newData.getPatientId() is null so existing value should be retained
        newData.setState(AppointmentStateEnum.BOOKED);

        Appointment updated = new Appointment();
        updated.setDoctorId(15);
        updated.setPatientId(20);
        updated.setState(AppointmentStateEnum.BOOKED);

        when(repository.findById(appointmentId)).thenReturn(Mono.just(existing));
        when(repository.save(any(Appointment.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(appointmentService.update(appointmentId, newData)).assertNext(app -> {
            assertEquals(15, app.getDoctorId());
            assertEquals(20, app.getPatientId());
            assertEquals(AppointmentStateEnum.BOOKED, app.getState());
        }).verifyComplete();
    }

    @Test
    public void testUpdate_NotFound() {
        int appointmentId = 1;
        Appointment newData = new Appointment();
        when(repository.findById(appointmentId)).thenReturn(Mono.empty());

        StepVerifier.create(appointmentService.update(appointmentId, newData))
                    .expectError(IllegalArgumentException.class)
                    .verify();
    }

    @Test
    public void testDeletePatientIdFromAppointment_Success() {
        int appointmentId = 1;
        Appointment appointment = new Appointment();
        appointment.setPatientId(123);
        Appointment savedAppointment = new Appointment();
        savedAppointment.setPatientId(null);

        when(repository.findById(appointmentId)).thenReturn(Mono.just(appointment));
        when(repository.save(any(Appointment.class))).thenReturn(Mono.just(savedAppointment));

        StepVerifier.create(appointmentService.deletePatientIdFromAppointment(appointmentId)).verifyComplete();

        verify(repository).save(argThat(app -> app.getPatientId() == null));
    }

    @Test
    public void testDeletePatientIdFromAppointment_NotFound() {
        int appointmentId = 1;
        when(repository.findById(appointmentId)).thenReturn(Mono.empty());

        StepVerifier.create(appointmentService.deletePatientIdFromAppointment(appointmentId))
                    .expectError(IllegalArgumentException.class)
                    .verify();
    }

    @Test
    public void testDeleteById_Success() {
        int appointmentId = 1;
        when(repository.existsById(appointmentId)).thenReturn(Mono.just(true));
        when(repository.deleteById(appointmentId)).thenReturn(Mono.empty());

        StepVerifier.create(appointmentService.deleteById(appointmentId)).verifyComplete();

        verify(repository).deleteById(appointmentId);
    }

    @Test
    public void testDeleteById_NotExist() {
        int appointmentId = 1;
        when(repository.existsById(appointmentId)).thenReturn(Mono.just(false));


        StepVerifier.create(appointmentService.deleteById(appointmentId))
                    .expectError(IllegalArgumentException.class)
                    .verify();
    }
}
