package org.example.docmeet.appointment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.docmeet.appointment.enums.AppointmentStateEnum;
import org.example.docmeet.appointment.enums.AppointmentTypeEnum;
import org.example.docmeet.constraints.ImmutableFieldConstraint;
import org.example.docmeet.validation.CreateValidation;
import org.example.docmeet.validation.UpdateValidation;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "appointment")
public class Appointment {

    @Id
    @NotBlank(groups = UpdateValidation.class)
    private Integer id;
    @ImmutableFieldConstraint(groups = UpdateValidation.class)
    @NotBlank(groups = CreateValidation.class)
    private Integer doctorId;
    @ImmutableFieldConstraint(groups = UpdateValidation.class)
    private Integer patientId;
    @NotBlank(groups = CreateValidation.class)
    private LocalDateTime dateTime;
    @NotBlank(groups = CreateValidation.class)
    private AppointmentTypeEnum type;
    @ImmutableFieldConstraint(groups = UpdateValidation.class)
    @NotBlank(groups = CreateValidation.class)
    private AppointmentStateEnum state;
    @NotBlank(groups = CreateValidation.class)
    private String address;
    private String note;
    private String patientNote;
    private BigDecimal rating;

}
