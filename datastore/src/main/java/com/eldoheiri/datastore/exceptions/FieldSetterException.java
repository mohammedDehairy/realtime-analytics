package com.eldoheiri.datastore.exceptions;

public final class FieldSetterException extends FieldAccessorsException {
    public FieldSetterException(String fieldName, Throwable cause) {
        super("Failed to invoke the setter of field: " + fieldName, cause);
        fieldName(fieldName);
    }
}
