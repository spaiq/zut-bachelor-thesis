package org.example.docmeet.review;

import jakarta.persistence.*;
import lombok.Data;
import org.example.docmeet.doctor.Doctor;
import org.example.docmeet.patient.Patient;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal rating;
}
