package org.example.docmeet.appointment;

import lombok.Builder;
import lombok.Data;
import org.example.docmeet.appointment.enums.AppointmentStateEnum;
import org.example.docmeet.appointment.enums.AppointmentTypeEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@Table(name = "appointment")
public class Appointment {

    @Id
    private Integer id;
    private Integer doctorId;
    private Integer patientId;
    private LocalDateTime dateTime;
    private AppointmentTypeEnum type;
    private AppointmentStateEnum state;
    private String note;
    private String patientNote;
    private BigDecimal rating;

}
