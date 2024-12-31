package com.eldoheiri.datastore.helpers;

import java.lang.reflect.Field;

public record SchemaRelation(Field field, String exportedForeignKeyName, Class<?> valueType) {
    
}
