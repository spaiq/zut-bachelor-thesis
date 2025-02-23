package org.example.docmeet.prescription;

import lombok.RequiredArgsConstructor;
import org.example.docmeet.authorization.IsAdmin;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository repository;

    @IsAdmin
    public Flux<Prescription> findAll() {
        return repository.findAll();
    }

    public Flux<Prescription> findAllById(Integer prescriptionId) {
        return repository.findAllById(prescriptionId);
    }

    @IsAdmin
    public Mono<Prescription> findById(Integer id) {
        return repository.findById(id);
    }

    public Mono<Prescription> findByAppointmentId(Integer appointmentId) {
        return repository.findByAppointmentId(appointmentId);
    }

    @PreAuthorize("hasAnyRole({'client_admin', 'client_doctor'})")
    public Mono<Prescription> save(Prescription prescription) {
        return repository.save(prescription);
    }

    @PreAuthorize("hasAnyRole({'client_admin', 'client_doctor'})")
    public Mono<Prescription> update(Integer id, Prescription prescription) {
        return repository.findById(id)
                         .switchIfEmpty(Mono.error(new IllegalArgumentException("Prescription with id %s not found".formatted(
                                 id))))
                         .flatMap(existingPrescription -> {
                             if (prescription.getAppointmentId() != null) {
                                 existingPrescription.setAppointmentId(prescription.getAppointmentId());
                             }
                             if (prescription.getDescription() != null) {
                                 existingPrescription.setDescription(prescription.getDescription());
                             }
                             return repository.save(existingPrescription);
                         });
    }


}
