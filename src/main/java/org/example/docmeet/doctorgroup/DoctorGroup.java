package org.example.docmeet.doctorgroup;

import jakarta.persistence.*;
import lombok.Data;
import org.example.docmeet.appointment.Appointment;
import org.example.docmeet.doctor.Doctor;

import java.util.Set;

@Entity
@Data
@Table(name = "doctor_group")
public class DoctorGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @OneToMany(mappedBy = "doctorGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Appointment> appointments;
}
