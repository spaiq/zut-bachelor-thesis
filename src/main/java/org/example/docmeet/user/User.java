package org.example.docmeet.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.docmeet.constraints.ImmutableFieldConstraint;
import org.example.docmeet.validation.CreateValidation;
import org.example.docmeet.validation.UpdateValidation;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
@Table(name = "user_account")
@Slf4j
public class User {

    @Id
    private Integer id;
    @ImmutableFieldConstraint(groups = UpdateValidation.class)
    @NotBlank(groups = CreateValidation.class)
    private String email;
    @NotBlank(groups = CreateValidation.class)
    private String name;
    private String secondName;
    @NotBlank(groups = CreateValidation.class)
    private String surname;
    @NotBlank(groups = CreateValidation.class)
    private String pesel;
    @Size(min = 9, max = 9)
    @NotBlank(groups = CreateValidation.class)
    private String phoneNumber;

    void logUserFound() {
        log.info("Found user with id {}: {}", id, this);
    }

    void logUserUpdated() {
        log.info("Updated user with id {}", id);
    }

    public User withoutPesel() {
        User u = this;
        u.setPesel(null);
        return u;
    }
}
