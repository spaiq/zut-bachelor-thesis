package org.example.docmeet.doctor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.docmeet.authorization.IsAdmin;
import org.example.docmeet.responses.DoctorResponse;
import org.example.docmeet.speciality.SpecialityRepository;
import org.example.docmeet.user.User;
import org.example.docmeet.user.UserRepository;
import org.example.docmeet.user.UserService;
import org.example.docmeet.utils.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    private final UserService userService;
    private final DoctorRepository repository;
    private final UserRepository userRepository;
    private final SpecialityRepository specialityRepository;

    public Flux<DoctorResponse> findAll() {
        return repository.findAll().flatMap(this::convertToFlux);
    }

    public Mono<DoctorResponse> findById(Integer id) {
        return repository.findById(id)
                         .doOnError(e -> log.error("Error while finding doctor with id {}", id, e))
                         .doOnSuccess(Doctor::logDoctorFound)
                         .flatMap(this::convertToMono);
    }

    public Mono<DoctorResponse> findByEmail(String email) {
        return userService.findByEmail(email)
                          .map(User::getId)
                          .flatMap(repository::findByUserId)
                          .doOnError(e -> log.error("Error while finding doctor with email {}", email, e))
                          .doOnSuccess(Doctor::logDoctorFound)
                          .flatMap(this::convertToMono);
    }

    public Mono<Page<DoctorResponse>> findBySpecialityId(Integer specialityId, Pageable pageable) {
        AtomicInteger size = new AtomicInteger();

        return repository.findBySpecialityId(specialityId, pageable)
                         .doOnError(e -> log.error("Error while finding doctors with speciality id {}",
                                                   specialityId,
                                                   e))
                         .flatMap(this::convertToFlux)
                         .doOnNext(ignored -> size.getAndIncrement())
                         .skip(pageable.getOffset())
                         .take(pageable.getPageSize())
                         .collectList()
                         .map(doctors -> new PageImpl<>(doctors, pageable, size.get()));
    }

    public Mono<String> findDoctorNameById(Integer id) {
        return repository.findById(id)
                         .doOnSuccess(o -> log.info("Doctor with id {} found", id))
                         .doOnError(e -> log.error("Error while finding doctor with id {}", id, e))
                         .flatMap(doctor -> userRepository.findById(doctor.getUserId())
                                                          .doOnSuccess(o -> log.info("User with id {} found",
                                                                                     doctor.getUserId()))
                                                          .doOnError(e -> log.error(
                                                                  "Error while finding user with id {}",
                                                                  doctor.getUserId(),
                                                                  e))
                                                          .map(user -> Utils.getFullName(user, doctor)))
                         .doOnSuccess(name -> log.info("Successfully retrieved full doctor name of {}", name))
                         .doOnError(e -> log.error("Error while retrieving full name of doctor with id {}", id, e));
    }

    @IsAdmin
    public Mono<DoctorResponse> save(Doctor doctor) {
        return repository.save(doctor).flatMap(this::convertToMono);
    }

    @IsAdmin
    public Mono<DoctorResponse> update(Integer id, Doctor doctor, ServerWebExchange exchange) {
        return repository.findById(id)
                         .switchIfEmpty(Mono.error(new NoResourceFoundException(exchange.getRequest()
                                                                                        .getPath()
                                                                                        .toString())))
                         .doOnError(e -> log.error("Error while updating doctor with id {}", id, e))
                         .doOnSuccess(Doctor::logDoctorFound)
                         .flatMap(existingDoctor -> {
                             if (doctor.getSpecialityId() != null) {
                                 existingDoctor.setSpecialityId(doctor.getSpecialityId());
                             }

                             return repository.save(existingDoctor);
                         })
                         .flatMap(this::convertToMono);

    }

    @IsAdmin
    public Mono<Void> deleteById(Integer id) {
        return repository.existsById(id)
                         .flatMap(bool -> bool ? Mono.empty() : Mono.error(new NoResourceFoundException(
                                 "Cannot delete. Doctor with id %s does not exist".formatted(id))))
                         .doOnError(e -> log.error("Error while deleting doctor with id {}", id, e))
                         .switchIfEmpty(repository.deleteById(id))
                         .doOnSuccess(o -> log.info("User with id {} deleted", id))
                .then();
    }

    public Mono<DoctorResponse> convertToMono(Doctor doctor) {
        return Mono.zip(userRepository.findById(doctor.getUserId()).map(User::withoutPesel),
                        specialityRepository.findById(doctor.getSpecialityId()),
                        (user, speciality) -> new DoctorResponse(doctor.getId(), user, speciality));
    }

    public Flux<DoctorResponse> convertToFlux(Doctor doctor) {
        return Flux.zip(userRepository.findById(doctor.getUserId()).map(User::withoutPesel),
                        specialityRepository.findById(doctor.getSpecialityId()),
                        (user, speciality) -> new DoctorResponse(doctor.getId(), user, speciality));
    }
}
