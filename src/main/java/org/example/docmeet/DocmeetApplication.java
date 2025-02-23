package org.example.docmeet;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SecurityScheme(name = "Keycloak",
                openIdConnectUrl = "http://localhost:15564/realms/docmeet/.well-known/openid-configuration",
                type = SecuritySchemeType.OPENIDCONNECT)
public class DocmeetApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocmeetApplication.class, args);
    }

}
