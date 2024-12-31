package com.eldoheiri.datastore.exceptions;

public class MapPredicateValueSerializationFailureException extends RuntimeException {
    private Integer valueIndex;

    public MapPredicateValueSerializationFailureException(Integer valueIndex, Throwable cause) {
        super("Predicate value at index " + valueIndex + " failed to be serialized into json string", cause);
    }

    public Integer getValueIndex() {
        return valueIndex;
    }

    public void setValueIndex(Integer valueIndex) {
        this.valueIndex = valueIndex;
    }


    @Override
    public String toString() {
        return "{" +
            " valueIndex='" + getValueIndex() + "'" +
            "}";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((valueIndex == null) ? 0 : valueIndex.hashCode());
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
        MapPredicateValueSerializationFailureException other = (MapPredicateValueSerializationFailureException) obj;
        if (valueIndex == null) {
            if (other.valueIndex != null)
                return false;
        } else if (!valueIndex.equals(other.valueIndex))
            return false;
        return true;
    }

}
