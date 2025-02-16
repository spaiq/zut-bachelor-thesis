package org.example.docmeet.speciality;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Data
@Table(name = "speciality")
public class Speciality {

    @Id
    private Integer id;
    private String name;
}
