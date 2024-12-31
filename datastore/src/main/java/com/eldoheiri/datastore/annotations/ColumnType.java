package com.eldoheiri.datastore.annotations;

public enum ColumnType {
    INT(Integer.class), TEXT(String.class);

    private Class<?> clazz;

    private ColumnType(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return this.clazz;
    }
}
