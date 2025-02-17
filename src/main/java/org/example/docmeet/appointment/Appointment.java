package org.example.docmeet.appointment;

import lombok.Builder;
import lombok.Data;
import org.example.docmeet.appointment.enums.AppointmentStateEnum;
import org.example.docmeet.appointment.enums.AppointmentTypeEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@Table(name = "appointment")
public class Appointment {

    @Id
    private Integer id;
    @NonNull
    private UUID doctorId;
    @NonNull
    private UUID patientId;
    @NonNull
    private LocalDateTime dateTime;
    @NonNull
    private AppointmentTypeEnum type;
    @NonNull
    private AppointmentStateEnum state;
    @NonNull
    private String note;
    private String patientNote;
    private BigDecimal rating;

}
