package com.eldoheiri.realtime_analytics.dataobjects.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.HashSet;

public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, String> {
    private Set<String> validValues = null;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return validValues.contains(value.toLowerCase());
    }

    @Override 
    public void initialize(EnumValidator constraintAnnotation) {
        validValues = new HashSet<String>();
        Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClass();

        Enum<?>[] enumValArray = enumClass.getEnumConstants();

        for (Enum<?> enumValue : enumValArray) {
            validValues.add(enumValue.toString().toLowerCase());
        }
    }
}
