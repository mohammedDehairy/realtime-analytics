package com.eldoheiri.realtime_analytics.dataobjects.validators;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public final class IsoLocaleValidator implements ConstraintValidator<IsoLocale, String> {
    private Set<String> isoLanguages;
    private Set<String> isoCountries;

    @Override
    public void initialize(IsoLocale constraintAnnotation) {
        isoLanguages = Arrays.stream(Locale.getISOLanguages())
                .map(code -> code.toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
        isoCountries = Arrays.stream(Locale.getISOCountries())
                .map(code -> code.toUpperCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        String normalized = value.replace('_', '-');
        String[] parts = normalized.split("-");

        if (parts.length == 1) {
            return isoLanguages.contains(parts[0].toLowerCase(Locale.ROOT));
        }

        if (parts.length == 2) {
            return isoLanguages.contains(parts[0].toLowerCase(Locale.ROOT))
                    && isoCountries.contains(parts[1].toUpperCase(Locale.ROOT));
        }

        return false;
    }
}
