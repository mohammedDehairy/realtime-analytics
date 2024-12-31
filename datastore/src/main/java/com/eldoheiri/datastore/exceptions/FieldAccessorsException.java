package com.eldoheiri.datastore.exceptions;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class FieldAccessorsException extends RuntimeException {
    private String fieldName;

    public FieldAccessorsException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public FieldAccessorsException fieldName(String fieldName) {
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
