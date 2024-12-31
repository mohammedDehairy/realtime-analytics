package com.eldoheiri.datastore.exceptions;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;

public final class FieldNotFoundException extends RuntimeException {
    private String fieldName;

    public FieldNotFoundException(String fieldName, Class<?> entityClazz) {
        super("No field found in : " + entityClazz + " with column name " + fieldName, null);
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String primaryKeyName) {
        this.fieldName = primaryKeyName;
    }

    public FieldNotFoundException primaryKeyName(String primaryKeyName) {
        setFieldName(primaryKeyName);
        return this;
    }

    @Override
    public boolean equals(Object o) {
      return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName);
    }

    @Override
    public String toString() {
        return "{" +
            " primaryKeyName='" + getFieldName() + "'" +
            "}";
    }
}
