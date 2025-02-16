package org.example.docmeet.doctor;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Builder
@Data
@Table(name = "doctor")
public class Doctor {

    @Id
    private Integer id;
    private Integer specialityId;
    private Integer userId;
    private String pwz;

}
