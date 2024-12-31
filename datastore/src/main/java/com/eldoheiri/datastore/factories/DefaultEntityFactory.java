package com.eldoheiri.datastore.factories;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.eldoheiri.datastore.annotations.DataBaseTable;
import com.eldoheiri.datastore.annotations.DatabaseColumn;
import com.eldoheiri.datastore.exceptions.FieldSetterException;
import com.eldoheiri.datastore.exceptions.UnsupportedTypeException;

public class DefaultEntityFactory implements EntityFactory {
    @Override
    public <Entity> List<Entity> fromResultSet(ResultSet resultSet, Class<Entity> clazz) {
        if (!clazz.isAnnotationPresent(DataBaseTable.class)) {
            throw new IllegalArgumentException("Entity must have DataBaseTable annotation");
        }
        List<Entity> result = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Entity newIntance = createFromResultSet(resultSet, clazz);
                if (newIntance == null) {
                    continue;
                }
                result.add(newIntance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
        
        return result;
    }

    private <Entity> Entity createFromResultSet(ResultSet resultSet, Class<Entity> clazz) {
        Entity newInstance;
        try {
            newInstance = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return null;
        }
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(DatabaseColumn.class)) {
                continue;
            }
            Class<?> fieldType = field.getType();
            DatabaseColumn columnAnnotation = field.getAnnotation(DatabaseColumn.class);
            try {
                Method setter = clazz.getMethod("s" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1), fieldType);
                if (String.class.isAssignableFrom(fieldType)) {
                    setter.invoke(newInstance, resultSet.getString(columnAnnotation.columnName()));
                } else if (Integer.class.isAssignableFrom(fieldType)) {
                    setter.invoke(newInstance, resultSet.getInt(columnAnnotation.columnName()));
                } else {
                    throw new UnsupportedTypeException().fieldName(field.getName()).type(fieldType);
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SQLException e) {
                throw new FieldSetterException(field.getName(), e);
            }
        }
        return newInstance;
    }
}
