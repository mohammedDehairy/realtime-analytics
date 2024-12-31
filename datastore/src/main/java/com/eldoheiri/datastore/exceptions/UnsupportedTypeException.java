package com.eldoheiri.datastore.exceptions;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;

public final class UnsupportedTypeException extends RuntimeException {
    private String fieldName;

    private Class<?> type;

    public UnsupportedTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedTypeException() {
        super("The ORM layer supports only fields of type String or Integer", null);
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Class<?> getType() {
        return this.type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public UnsupportedTypeException fieldName(String fieldName) {
        setFieldName(fieldName);
        return this;
    }

    public UnsupportedTypeException type(Class<?> type) {
        setType(type);
        return this;
    }

    @Override
    public boolean equals(Object o) {
      return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName, type);
    }

    @Override
    public String toString() {
        return "{" +
            " fieldName='" + getFieldName() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
    
}
