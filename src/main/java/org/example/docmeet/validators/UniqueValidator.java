package org.example.docmeet.validators;

import org.example.docmeet.annotations.Unique;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import reactor.core.publisher.Mono;

@Component
public class UniqueValidator implements ConstraintValidator<Unique, String> {

    private final DatabaseClient databaseClient;

    private String tableName;
    private String columnName;

    public UniqueValidator(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public void initialize(Unique constraintAnnotation) {
        this.tableName = constraintAnnotation.tableName();
        this.columnName = constraintAnnotation.columnName();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s = :value", tableName, columnName);

        Mono<Boolean> isUnique = databaseClient.sql(sql)
                .bind("value", value)
                .fetch()
                .one()
                .map(result -> (Long) result.get("count") == 0);

        return Boolean.TRUE.equals(isUnique.block());
    }
}
