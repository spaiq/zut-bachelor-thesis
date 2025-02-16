package org.example.docmeet.user;

import lombok.Builder;
import lombok.Data;
import org.example.docmeet.user.enums.TotpAlgorithmEnum;
import org.example.docmeet.user.enums.UserRoleEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Data
@Table(name = "user")
public class User {

    @Id
    private Integer id;
    private String email;
    private String password;
    private String seed;
    private TotpAlgorithmEnum totpAlgorithm;
    private String name;
    private String secondName;
    private String surname;
    private String pesel;
    private String phoneNumber;
    private UserRoleEnum role;
}
