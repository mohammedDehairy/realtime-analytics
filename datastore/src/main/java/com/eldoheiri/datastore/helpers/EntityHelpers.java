package com.eldoheiri.datastore.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.eldoheiri.datastore.annotations.DatabaseColumn;
import com.eldoheiri.datastore.annotations.TableRelation;
import com.eldoheiri.datastore.exceptions.DuplicateFieldsException;
import com.eldoheiri.datastore.exceptions.FieldGetterException;
import com.eldoheiri.datastore.exceptions.FieldNotFoundException;
import com.eldoheiri.datastore.exceptions.FieldSetterException;
import com.eldoheiri.datastore.exceptions.UnsupportedTypeException;

public interface EntityHelpers {
    public static <ValueType> ValueType getValueOf(Field field, Object entity, Class<ValueType> valueClass) {
        try {
            Method getter = entity.getClass().getMethod("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1));
            Class<?> fieldType = field.getType();
            Object value = getter.invoke(entity);
            if (value == null) {
                return null;
            }
            if (!(valueClass.isAssignableFrom(fieldType)) || !(valueClass.isAssignableFrom(value.getClass()))) {
                throw new UnsupportedTypeException().fieldName(field.getName()).type(fieldType);
            }
            return (ValueType)value;
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new FieldGetterException(field.getName(), e);
        }
    }

    public static void setValueOf(Field field, Object entity, Object value) {
        try {
            Method setter = entity.getClass().getMethod("set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1), value.getClass());
            setter.invoke(entity, value);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new FieldSetterException(field.getName(), e);
        }
    }

    public static Field findField(Object entity, String columnName) {
        List<Field> fields = findFields(entity.getClass(), DatabaseColumn.class).stream().filter(f -> {
            DatabaseColumn column = f.getAnnotation(DatabaseColumn.class);
            return column.columnName().equals(columnName);
        }).collect(Collectors.toList());
        if (fields.size() < 1) {
            throw new FieldNotFoundException(columnName, entity.getClass());
        }

        if (fields.size() > 1) {
            throw new DuplicateFieldsException(columnName, entity.getClass());
        }
        return fields.get(0);
    }

    public static List<Field> findFields(Class<?> entityClazz, Class<? extends Annotation> annotationClass) {
        List<Field> fields = Arrays.stream(entityClazz.getDeclaredFields())
        .filter(f -> f.isAnnotationPresent(annotationClass))
        .collect(Collectors.toList());
        return fields;
    }

    public static List<ConcreteRelation> findRelations(Object entity) {
        return findFields(entity.getClass(), TableRelation.class)
        .stream()
        .map(field -> {
            TableRelation tableRelation = field.getAnnotation(TableRelation.class);
            Object value = getValueOf(field, entity, Object.class);
            return new ConcreteRelation(field, tableRelation.exportedForeignKeyName(), value, field.getType());
        }).collect(Collectors.toList());
    }

    public static List<SchemaRelation> findSchemaRelations(Class<?> entityClass) {
        return findFields(entityClass, TableRelation.class)
        .stream()
        .map(field -> {
            TableRelation tableRelation = field.getAnnotation(TableRelation.class);
            return new SchemaRelation(field, tableRelation.exportedForeignKeyName(), field.getType());
        }).collect(Collectors.toList());
    }
}
