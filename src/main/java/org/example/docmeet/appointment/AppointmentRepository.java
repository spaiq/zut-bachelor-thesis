package org.example.docmeet.appointment;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AppointmentRepository extends ReactiveCrudRepository<Appointment, Integer> {
}
