package org.example.docmeet.doctor;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.example.docmeet.validation.CreateValidation;
import org.example.docmeet.validation.UpdateValidation;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Builder
@Data
@Table(name = "doctor")
public class Doctor {

    @Id
    @NotBlank(groups = UpdateValidation.class)
    private UUID id;
    @NotBlank(groups = CreateValidation.class)
    private UUID userId;
    @NotBlank(groups = CreateValidation.class)
    private Integer specialityId;

}
