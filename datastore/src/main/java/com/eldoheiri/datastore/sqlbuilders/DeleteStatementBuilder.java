package com.eldoheiri.datastore.sqlbuilders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.Map;

import com.eldoheiri.datastore.annotations.DataBaseTable;
import com.eldoheiri.datastore.exceptions.MapPredicateValueSerializationFailureException;
import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.buildersinterfaces.IPredicate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DeleteStatementBuilder implements IDeleteStatementBuilder {

    @Override
    public PreparedStatement deleteStatement(IPredicate predicate, Class<?> entityClass, Connection connection, boolean cascade) throws SQLException {
        if (!entityClass.isAnnotationPresent(DataBaseTable.class)) {
            throw new IllegalArgumentException("Entity of type: " + entityClass + " must be annotated with @DataBaseTable");
        }
        DataBaseTable tableAnnotation = entityClass.getAnnotation(DataBaseTable.class);
        String tableName = tableAnnotation.tableName();

        String query = "Delete From " + tableName + " where " + predicate.sqlPredicate() + (cascade ? " CASCADE;" : ";");
        PreparedStatement statement = connection.prepareStatement(query);

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
    
}
