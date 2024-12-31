package com.eldoheiri.realtime_analytics.dataobjects.validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;

@Constraint(validatedBy = EnumValidatorImpl.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@NotNull(message = "Value cannot be null")
public @interface EnumValidator {
    Class<? extends Enum<?>> enumClass();

    String message() default "Value is not Valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
