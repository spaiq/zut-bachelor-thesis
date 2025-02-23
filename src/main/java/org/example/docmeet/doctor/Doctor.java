package org.example.docmeet.doctor;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.docmeet.validation.CreateValidation;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Data
@Table(name = "doctor")
@Slf4j
public class Doctor {

    @Id
    private Integer id;
    @NotBlank(groups = CreateValidation.class)
    private Integer userId;
    @NotBlank(groups = CreateValidation.class)
    private Integer specialityId;
    private String title;

    void logDoctorFound() {
        log.info("Found doctor with id {}: {}", id, this);
    }

}
