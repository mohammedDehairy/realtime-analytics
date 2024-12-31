package com.eldoheiri.datastore.helpers;

import java.lang.reflect.Field;

public record ConcreteRelation(Field field, String exportedForeignKeyName, Object value, Class<?> valueType) {
}
