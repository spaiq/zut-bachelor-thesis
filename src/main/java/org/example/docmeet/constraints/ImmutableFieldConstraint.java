package org.example.docmeet.constraints;

import org.example.docmeet.constraints.validation.ImmutableFieldConstraintValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR,
        ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImmutableFieldConstraintValidator.class)
public @interface ImmutableFieldConstraint {
    String message() default "Field cannot be changed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
