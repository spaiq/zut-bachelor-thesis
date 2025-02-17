package org.example.docmeet.user;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;

import java.util.UUID;

@Builder
@Data
@Table(name = "user")
public class User {

    @Id
    private UUID id;
    @NonNull
    private String email;
    @NonNull
    private String name;
    @NonNull
    private String secondName;
    @NonNull
    private String surname;
    @NonNull
    private String pesel;
    @NonNull
    private String phoneNumber;
}
