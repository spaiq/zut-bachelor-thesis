package org.example.docmeet.prescription;

import jakarta.persistence.*;
import lombok.Data;
import org.example.docmeet.doctor.Doctor;
import org.example.docmeet.patient.Patient;

@Entity
@Data
@Table(name = "prescription")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(nullable = false)
    private String code;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String note;
}
