package org.example.docmeet.prescription;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.docmeet.appointment.Appointment;
import org.example.docmeet.appointment.AppointmentService;
import org.example.docmeet.user.User;
import org.example.docmeet.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@SecurityRequirement(name = "Keycloak")
@Slf4j
public class PrescriptionController {

    private final PrescriptionService service;
    private final AppointmentService appointmentService;
    private final UserService userService;

    @GetMapping("/prescriptions")
    public Flux<Prescription> findAll() {
        return service.findAll().doOnError(e -> log.error("Error while finding all prescriptions", e));
    }

    @GetMapping("/prescription/{id}")
    public Mono<Prescription> findById(@PathVariable Integer id) {
        return service.findById(id);
    }

    @GetMapping("/prescription/appointment/{appointmentId}")
    public Mono<Prescription> findByAppointmentId(@PathVariable Integer appointmentId) {
        return service.findByAppointmentId(appointmentId);
    }

    @PreAuthorize("hasAnyRole({'client_doctor', 'client_user'})")
    @GetMapping("/prescriptions/current")
    public Mono<Page<Prescription>> findCurrentUser(@RequestParam(defaultValue = "0") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer size,
                                                    Authentication authentication) {

        AtomicInteger resultSize = new AtomicInteger();

        return userService.findByEmail(authentication.getName())
                          .log()
                          .map(User::getId)
                          .map(appointmentService::findByPatientId)
                          .log()
                          .flatMapMany(appointment -> appointment.map(Appointment::getId))
                          .log()
                          .flatMap(service::findByAppointmentId)
                          .log()
                          .doOnNext(ignore -> resultSize.getAndIncrement())
                          .skip(Integer.toUnsignedLong(page * size))
                          .take(size)
                          .collectList()
                          .map(prescriptions -> new PageImpl<>(prescriptions,
                                                               Pageable.ofSize(size).withPage(page),
                                                               resultSize.get()));
    }

    @PostMapping("/prescription")
    public Mono<Prescription> save(@RequestBody Prescription prescription) {
        return service.save(prescription);
    }

    @PatchMapping("/prescription/{id}")
    public Mono<Prescription> update(@PathVariable Integer id, @RequestBody Prescription prescription) {
        return service.update(id, prescription);
    }


}
