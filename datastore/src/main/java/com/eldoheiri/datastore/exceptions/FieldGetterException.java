package com.eldoheiri.datastore.exceptions;

public final class FieldGetterException extends FieldAccessorsException {
    public FieldGetterException(String fieldName, Throwable cause) {
        super("Failed to invoke the getter of field: " + fieldName, cause);
        fieldName(fieldName);
    }
}
