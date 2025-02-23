package org.example.docmeet.constraints.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.docmeet.constraints.ImmutableFieldConstraint;

public class ImmutableFieldConstraintValidator implements ConstraintValidator<ImmutableFieldConstraint, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return value == null;
    }
}
