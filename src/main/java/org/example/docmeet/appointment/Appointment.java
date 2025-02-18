package org.example.docmeet.appointment;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.example.docmeet.appointment.enums.AppointmentStateEnum;
import org.example.docmeet.appointment.enums.AppointmentTypeEnum;
import org.example.docmeet.validation.CreateValidation;
import org.example.docmeet.validation.UpdateValidation;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@Table(name = "appointment")
public class Appointment {

    @Id
    @NotBlank(groups = UpdateValidation.class)
    private Integer id;
    @NotBlank(groups = CreateValidation.class)
    private UUID doctorId;
    @NotBlank(groups = CreateValidation.class)
    private UUID patientId;
    @NotBlank(groups = CreateValidation.class)
    private LocalDateTime dateTime;
    @NotBlank(groups = CreateValidation.class)
    private AppointmentTypeEnum type;
    @NotBlank(groups = CreateValidation.class)
    private AppointmentStateEnum state;
    @NotBlank(groups = CreateValidation.class)
    private String note;
    private String patientNote;
    private BigDecimal rating;

}
