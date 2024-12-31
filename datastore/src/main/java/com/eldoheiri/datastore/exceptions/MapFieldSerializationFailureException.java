package com.eldoheiri.datastore.exceptions;

public class MapFieldSerializationFailureException extends RuntimeException {
    private String fieldName;

    public MapFieldSerializationFailureException(String fieldName, Class<?> entityClazz, Throwable cause) {
        super("Map field " + fieldName + " found in : " + entityClazz + " failed to be serialized into json field", cause);
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        return "{" +
            " fieldName='" + getFieldName() + "'" +
            "}";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MapFieldSerializationFailureException other = (MapFieldSerializationFailureException) obj;
        if (fieldName == null) {
            if (other.fieldName != null)
                return false;
        } else if (!fieldName.equals(other.fieldName))
            return false;
        return true;
    }

}
