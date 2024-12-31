package com.eldoheiri.datastore.sqlbuilders;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.eldoheiri.datastore.annotations.DataBaseTable;
import com.eldoheiri.datastore.annotations.DatabaseColumn;
import com.eldoheiri.datastore.exceptions.MapFieldSerializationFailureException;
import com.eldoheiri.datastore.exceptions.UnsupportedTypeException;
import com.eldoheiri.datastore.helpers.EntityHelpers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class InsertStatementBuilder implements IInsertStatementBuilder{
    public PreparedStatement insertStatement(Object entity,
            Connection connection) throws SQLException {
        if (!entity.getClass().isAnnotationPresent(DataBaseTable.class)) {
            throw new IllegalArgumentException(
                    "Entity of type: " + entity.getClass() + " must be annotated with @DataBaseTable");
        }
        DataBaseTable tableAnnotation = entity.getClass().getAnnotation(DataBaseTable.class);
        String tableName = tableAnnotation.tableName();
        String primaryKeyColumn = tableAnnotation.primaryKeyColumn();
        List<ConcreteAttribute> attributes = getAllAttributes(entity, primaryKeyColumn).stream().filter(kv -> kv.value() != null).collect(Collectors.toList());
        StringBuilder queryString = new StringBuilder();
        queryString.append("Insert into " + tableName + " (");
        queryString.append(attributes.stream().map(ConcreteAttribute::columnName).collect(Collectors.joining(" ,")) + ")");
        queryString.append(" values (");
        queryString.append(attributes.stream().map(t -> "?" + (t.castToTypeName().isEmpty() ? "" : "::" + t.castToTypeName())).collect(Collectors.joining(" ,")) + ");");
        PreparedStatement statement = connection.prepareStatement(queryString.toString(), Statement.RETURN_GENERATED_KEYS);
        for (int i = 1; i <= attributes.size(); i++) {
            ConcreteAttribute keyValuePair = attributes.get(i - 1);
            Class<?> type = keyValuePair.type();
            if (keyValuePair.value() == null) {
                continue;
            }
            if (Integer.class.isAssignableFrom(type) && (keyValuePair.value() instanceof Integer integer)) {
                statement.setInt(i, integer);
            } else if (Long.class.isAssignableFrom(type) && (keyValuePair.value() instanceof Long longValue)) {
                statement.setLong(i, longValue);
            } else if (String.class.isAssignableFrom(type) && (keyValuePair.value() instanceof String string)) {
                statement.setString(i, string);
            } else if (java.sql.Timestamp.class.isAssignableFrom(type) && (keyValuePair.value() instanceof java.sql.Timestamp timestamp)) {
                statement.setTimestamp(i, timestamp);
            } else if (Map.class.isAssignableFrom(type) && (keyValuePair.value() instanceof Map map)) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    statement.setString(i, objectMapper.writeValueAsString(map));
                } catch (JsonProcessingException e) {
                    throw new MapFieldSerializationFailureException(keyValuePair.fieldName, entity.getClass(), e);
                }
            } else {
                throw new UnsupportedTypeException().fieldName(keyValuePair.fieldName()).type(type);
            }
        }
        return statement;
    }

    private List<ConcreteAttribute> getAllAttributes(Object entity, String primaryKeyColumn) {
        Field[] fields = entity.getClass().getDeclaredFields();
        List<ConcreteAttribute> attributes = new ArrayList<>();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(DatabaseColumn.class)) {
                continue;
            }
            if (field.getName().equals(primaryKeyColumn)) {
                continue;
            }
            DatabaseColumn columnAnnotation = field.getAnnotation(DatabaseColumn.class);
            Object value = EntityHelpers.getValueOf(field, entity, Object.class);
            attributes.add(
                    new ConcreteAttribute(columnAnnotation.columnName(), columnAnnotation.castToTypeName(), value, field.getName(), field.getType()));
        }
        return attributes;
    }

    private static record ConcreteAttribute(String columnName, String castToTypeName, Object value, String fieldName, Class<?> type) {
    }
}
