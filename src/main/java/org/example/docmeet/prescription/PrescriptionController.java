package org.example.docmeet.prescription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionRepository repository;

    public Flux<Prescription> findAll() {
        return repository.findAll();
    }
}
