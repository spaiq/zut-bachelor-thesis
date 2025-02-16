package org.example.docmeet.prescription;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Data
@Table(name = "prescription")
public class Prescription {

    @Id
    private Integer id;
    private Integer appointmentId;
    private String code;
    private String description;
}
