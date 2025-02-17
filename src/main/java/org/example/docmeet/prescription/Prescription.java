package org.example.docmeet.prescription;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Builder
@Data
@Table(name = "prescription")
public class Prescription {

    @Id
    private Integer id;
    @NonNull
    private Integer appointmentId;
    @NonNull
    private LocalDateTime dateTime;
    @NonNull
    private String description;
}
