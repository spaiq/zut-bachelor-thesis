package org.example.docmeet.user;

import jakarta.persistence.*;
import lombok.Data;
import org.example.docmeet.doctor.Doctor;
import org.example.docmeet.patient.Patient;

@Entity
@Data
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 320)
    private String email;

    @Column(nullable = false, length = 128)
    private String password;

    @Column(nullable = false, length = 256)
    private String seed;

    @Column(nullable = false, length = 10)
    private String totpAlgorithm;

    @Column(nullable = false)
    private String name;

    @Column
    private String secondName;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false, length = 11)
    private String pesel;

    @Column(nullable = false, length = 9)
    private String phoneNumber;

    @Column(nullable = false)
    private Boolean isAdmin = false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Doctor doctor;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Patient patient;
}
