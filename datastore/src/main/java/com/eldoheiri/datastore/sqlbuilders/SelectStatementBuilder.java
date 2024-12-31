package com.eldoheiri.datastore.sqlbuilders;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.eldoheiri.datastore.annotations.DataBaseTable;
import com.eldoheiri.datastore.annotations.DatabaseColumn;
import com.eldoheiri.datastore.exceptions.MapPredicateValueSerializationFailureException;
import com.eldoheiri.datastore.helpers.EntityHelpers;
import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.buildersinterfaces.IPredicate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class SelectStatementBuilder implements ISelectStatementBuilder {
    public PreparedStatement selectStatement(IPredicate predicate, Class<?> entityClass, Connection connection) throws SQLException {
        if (!entityClass.isAnnotationPresent(DataBaseTable.class)) {
            throw new IllegalArgumentException("Entity of type: " + entityClass + " must be annotated with @DataBaseTable");
        }
        SchemaEntity schemaEntity = parseSchema(entityClass);
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("Select ");
        List<Attribute> attributes = schemaEntity.attributes();
        queryBuilder.append(attributes.stream().map(Attribute::columnName).collect(Collectors.joining(", ")));
        queryBuilder.append("from " + schemaEntity.tableName);
        queryBuilder.append("where " + predicate.sqlPredicate() + ";");
        PreparedStatement statement = connection.prepareStatement(queryBuilder.toString());
        for (int index = 0; index < predicate.predicateValues().size(); index++) {
            var value = predicate.predicateValues().get(index);
            if (value.value() instanceof String string) {
                statement.setString(index + 1, string);
            } else if (value.value() instanceof Integer integer) {
                statement.setInt(index + 1, integer);
            } else if (value.value() instanceof Long longValue) {
                statement.setLong(index + 1, longValue);
            } else if (value.value() instanceof java.sql.Timestamp timestamp) {
                statement.setTimestamp(index + 1, timestamp);
            } else if (value.value() instanceof Map map) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    statement.setString(index + 1, objectMapper.writeValueAsString(map));
                } catch (JsonProcessingException e) {
                    throw new MapPredicateValueSerializationFailureException(index, e);
                }
            } else {
                throw new IllegalArgumentException("Only String, integer, Timestamp, or Map<String, Object> values are allowed in parameter value");
            }
        }
        return statement;
    }

    private SchemaEntity parseSchema(Class<?> entityClass) {
        if (!entityClass.isAnnotationPresent(DataBaseTable.class)) {
            throw new IllegalArgumentException(
                    "Entity of type: " + entityClass + " must be annotated with @DataBaseTable");
        }
        DataBaseTable tableAnnotation = entityClass.getAnnotation(DataBaseTable.class);
        String tableName = tableAnnotation.tableName();
        return new SchemaEntity(tableName, entityClass, parseRelations(entityClass), parseAttributes(entityClass, tableName));
    }

    private List<Attribute> parseAttributes(Class<?> entityClass, String tableName) {
        Field[] fields = entityClass.getDeclaredFields();
        List<Attribute> attributes = new ArrayList<>();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(DatabaseColumn.class)) {
                continue;
            }
            DatabaseColumn columnAnnotation = field.getAnnotation(DatabaseColumn.class);
            attributes.add(
                    new Attribute(tableName, columnAnnotation.columnName(), field.getName(), field.getType()));
        }
        return attributes;
    }

    private List<SchemaRelation> parseRelations(Class<?> entityClass) {
        List<com.eldoheiri.datastore.helpers.SchemaRelation> relations = EntityHelpers.findSchemaRelations(entityClass);
        return relations.stream().map(relation -> {
            SchemaEntity schemaEntity = parseSchema(relation.valueType());
            return new SchemaRelation(schemaEntity, relation.exportedForeignKeyName());
        }).collect(Collectors.toList());
    }

    private static record SchemaEntity(String tableName, Class<?> entityClass, List<SchemaRelation> relations, List<Attribute> attributes) {
    }

    private static record SchemaRelation(SchemaEntity refererEntity, String foreignKeyName) {
    }

    private static record Attribute(String tableName, String columnName, String fieldName, Class<?> type) {
    }
}
