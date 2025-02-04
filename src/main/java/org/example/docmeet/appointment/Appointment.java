package org.example.docmeet.appointment;

import jakarta.persistence.*;
import lombok.Data;
import org.example.docmeet.doctorgroup.DoctorGroup;
import org.example.docmeet.patient.Patient;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "doctor_group_id", nullable = false)
    private DoctorGroup doctorGroup;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String state = "available";

    @Column
    private String note;
}
