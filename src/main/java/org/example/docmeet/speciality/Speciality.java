package org.example.docmeet.speciality;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.example.docmeet.annotations.Unique;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;

@Builder
@Data
@Table(name = "speciality")
public class Speciality {

    @Id
    private Integer id;
    @NotBlank
    private String name;
}
