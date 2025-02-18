package org.example.docmeet.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.example.docmeet.validation.CreateValidation;
import org.example.docmeet.validation.UpdateValidation;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Builder
@Data
@Table(name = "users")
public class User {

    @Id
    @NotBlank(groups = UpdateValidation.class)
    private UUID id;
    @NotBlank(groups = CreateValidation.class)
    private String email;
    @NotBlank(groups = CreateValidation.class)
    private String name;
    @NotBlank(groups = CreateValidation.class)
    private String secondName;
    @NotBlank(groups = CreateValidation.class)
    private String surname;
    @NotBlank(groups = CreateValidation.class)
    private String pesel;
    @NotBlank(groups = CreateValidation.class)
    private String phoneNumber;
}
