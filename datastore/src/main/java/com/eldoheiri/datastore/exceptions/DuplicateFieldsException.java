package com.eldoheiri.datastore.exceptions;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class DuplicateFieldsException extends RuntimeException {
    private String fieldName;

    public DuplicateFieldsException(String fieldName, Class<?> entityClazz) {
        super("Multiple fields found in : " + entityClazz + " that matchs column name " + fieldName, null);
        fieldName(fieldName);
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public DuplicateFieldsException fieldName(String fieldName) {
        setFieldName(fieldName);
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
            " fieldName='" + getFieldName() + "'" +
            "}";
    }

}
