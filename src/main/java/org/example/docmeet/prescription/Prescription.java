package org.example.docmeet.prescription;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.example.docmeet.validation.CreateValidation;
import org.example.docmeet.validation.UpdateValidation;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Builder
@Data
@Table(name = "prescription")
public class Prescription {

    @Id
    @NotBlank(groups = UpdateValidation.class)
    private Integer id;
    @NotBlank(groups = CreateValidation.class)
    private Integer appointmentId;
    @NotBlank(groups = CreateValidation.class)
    private LocalDateTime dateTime;
    @NotBlank(groups = CreateValidation.class)
    private String description;
}
