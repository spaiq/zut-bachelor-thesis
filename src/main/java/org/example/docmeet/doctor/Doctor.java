package org.example.docmeet.doctor;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;

import java.util.UUID;

@Builder
@Data
@Table(name = "doctor")
public class Doctor {

    @Id
    private UUID id;
    @NonNull
    private UUID userId;
    @NonNull
    private Integer specialityId;

}
