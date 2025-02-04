package org.example.docmeet.doctor;

import jakarta.persistence.*;
import lombok.Data;
import org.example.docmeet.doctorgroup.DoctorGroup;
import org.example.docmeet.prescription.Prescription;
import org.example.docmeet.review.Review;
import org.example.docmeet.user.User;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Data
@Table(name = "doctor")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String pzw;

    @Column
    private BigDecimal rating;

    @Column(nullable = false)
    private String speciality;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<DoctorGroup> doctorGroups;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Review> reviews;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Prescription> prescriptions;

}
