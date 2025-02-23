package org.example.docmeet.appointment;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.docmeet.appointment.enums.AppointmentStateEnum;
import org.example.docmeet.authorization.IsUser;
import org.example.docmeet.doctor.DoctorService;
import org.example.docmeet.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@SecurityRequirement(name = "Keycloak")
@Slf4j
public class AppointmentController {

    private final AppointmentService service;
    private final DoctorService doctorService;
    private final UserService userService;

    @GetMapping("/appointments")
    public Flux<Appointment> findAll() {
        return service.findAll().doOnError(e -> log.error("Error while finding all appointments", e));
    }

    @GetMapping("/doctor/{doctorId}/appointments")
    public Mono<Page<Appointment>> findByDoctorId(@PathVariable Integer doctorId,
                                                  @RequestParam(defaultValue = "0") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        return service.findByDoctorId(doctorId, Pageable.ofSize(size).withPage(page));
    }

    @GetMapping("/patient/{patientId}/appointments")
    public Mono<Page<Appointment>> findByPatientId(@PathVariable Integer patientId,
                                                   @RequestParam(defaultValue = "0") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return service.findByPatientId(patientId, Pageable.ofSize(size).withPage(page));
    }

    @GetMapping("/appointments/speciality/{specialityId}/available")
    public Mono<Page<Appointment>> findAvailableBySpecialityId(@PathVariable Integer specialityId,
                                                               @RequestParam(defaultValue = "0") Integer page,
                                                               @RequestParam(defaultValue = "10") Integer size,
                                                               @RequestParam(required = false) LocalDateTime from,
                                                               @RequestParam(required = false) LocalDateTime to) {
        return service.findAvailableBySpecialityId(specialityId, Pageable.ofSize(size).withPage(page), from, to)
                      .doOnError(e -> log.error(
                              "Error while finding available appointments with speciality id {} and params page: {}, size: {}, from: {}, to: {}",
                              specialityId,
                              page,
                              size,
                              from,
                              to,
                              e));
    }

    @GetMapping("/appointments/doctor/{doctorId}/available")
    public Mono<Page<Appointment>> findByDoctorIdAndStateAvailable(@PathVariable Integer doctorId,
                                                                   @RequestParam(defaultValue = "0") Integer page,
                                                                   @RequestParam(defaultValue = "10") Integer size) {
        return service.findByDoctorIdAndStateAvailable(doctorId, Pageable.ofSize(size).withPage(page));
    }

    @GetMapping("/appointment/{appointmentId}")
    public Mono<Appointment> findById(@PathVariable Integer appointmentId) {
        return service.findById(appointmentId);
    }

    @PreAuthorize("hasAnyRole({'client_admin', 'client_doctor'})")
    @PostMapping("/appointment")
    public Mono<Appointment> save(@RequestBody Appointment appointment,
                                  ServerWebExchange exchange,
                                  Authentication authentication) {
        return doctorService.findById(appointment.getDoctorId())
                            .map(doctor -> authentication.getAuthorities()
                                                         .contains(new SimpleGrantedAuthority("ROLE_client_admin")) ||
                                           doctor.getUser().getEmail().equals(authentication.getName()))
                            .flatMap(isPermitted -> isPermitted ? Mono.empty() :
                                    Mono.error(new AccessDeniedException("Access to denied for %s".formatted(
                                            authentication.getName()))))
                            .doOnSuccess(o -> log.info("Granted access for {}",

                                                       authentication.getName()))
                .then(service.save(appointment))
                .doOnSuccess(savedAppointment -> {
                    exchange.getResponse().setStatusCode(HttpStatus.CREATED);
                    log.info("Appointment {} saved", savedAppointment);
                    URI location = exchange.getRequest().getURI();
                    exchange.getResponse()
                            .getHeaders()
                            .setLocation(location.resolve(location.getPath() + "/" + savedAppointment.getId()));
                    log.info("Appointment {} saved", savedAppointment);
                })
                .doOnError(e -> log.error("Error while saving appointment {}", appointment, e));
    }

    @PreAuthorize("hasAnyRole({'client_admin', 'client_doctor'})")
    @PatchMapping("/appointment/{appointmentId}")
    public Mono<Appointment> update(@PathVariable Integer appointmentId,
                                    @RequestBody Appointment appointment,
                                    Authentication authentication) {
        return service.update(appointmentId, appointment)
                      .doOnSuccess(updatedAppointment -> log.info("Appointment {} updated", updatedAppointment))
                      .doOnError(e -> log.error("Error while updating appointment {}", appointment, e));
    }

    @PreAuthorize("hasAnyRole({'client_admin', 'client_doctor'})")
    @DeleteMapping("/appointment/{appointmentId}")
    public Mono<Void> deleteById(@PathVariable Integer appointmentId) {
        return service.deleteById(appointmentId);
    }

    @IsUser
    @PostMapping("/appointment/{appointmentId}/book")
    public Mono<Void> book(@PathVariable Integer appointmentId, Authentication authentication) {
        return userService.findByEmail(authentication.getName())
                          .flatMap(patient -> service.update(appointmentId,
                                                             Appointment.builder()
                                                                        .state(AppointmentStateEnum.BOOKED)
                                                                        .patientId(patient.getId())
                                                                        .build()))
                          .doOnSuccess(bookedAppointment -> log.info("Appointment {} booked", bookedAppointment))
                          .doOnError(e -> log.error("Error while booking appointment {}", appointmentId, e))
                .then();
    }

    @IsUser
    @PostMapping("/appointment/{appointmentId}/cancel")
    public Mono<Void> cancelUser(@PathVariable Integer appointmentId, Authentication authentication) {
        return userService.findByEmail(authentication.getName())
                          .map(patient -> service.findByPatientId(patient.getId())
                                                 .filter(appointment -> appointment.getId().equals(appointmentId))
                                                 .switchIfEmpty(Mono.error(new AccessDeniedException(
                                                         "Access to appointment %s denied for %s".formatted(
                                                                 appointmentId,
                                                                 authentication.getName())))))
                          .flatMap(appointments -> service.update(appointmentId,
                                                                  Appointment.builder()
                                                                             .state(AppointmentStateEnum.CANCELLED_BY_PATIENT)
                                                                             .build()))
                          .doOnSuccess(bookedAppointment -> log.info("Appointment {} cancelled by patient",
                                                                     bookedAppointment))
                .then(service.deletePatientIdFromAppointment(appointmentId))
                .log()
                .doOnError(e -> log.error("Error while booking appointment {}", appointmentId, e))
                .then();
    }
}
