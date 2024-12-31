package com.eldoheiri.datastore.implementations.insertion;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.eldoheiri.datastore.annotations.DataBaseTable;
import com.eldoheiri.datastore.exceptions.UnsupportedTypeException;
import com.eldoheiri.datastore.helpers.ConcreteRelation;
import com.eldoheiri.datastore.helpers.EntityHelpers;
import com.eldoheiri.datastore.sqlbuilders.SqlStatementBuilder;

public final class DataStoreInsertor implements IDataStoreInsertor {
    
    public <Entity> boolean insert(Entity entity, Connection dbConnection) throws SQLException {
        Objects.requireNonNull(entity);
        if (!entity.getClass().isAnnotationPresent(DataBaseTable.class)) {
            throw new IllegalArgumentException(
                    "Entity of type: " + entity.getClass() + " must be annotated with @DataBaseTable");
        }
        SqlStatementBuilder sqlStatementBuilder = new SqlStatementBuilder();
        dbConnection.setAutoCommit(false);
        insert(entity, sqlStatementBuilder, dbConnection);
        return true;
    }

    public <Entity> int insert(List<Entity> entities, Connection dbConnection) throws SQLException {
        Objects.requireNonNull(entities);
        if (entities.size() == 0) {
            return 0;
        }
        if (entities.stream().anyMatch(entity -> !entity.getClass().isAnnotationPresent(DataBaseTable.class))) {
            throw new IllegalArgumentException("Entity class must be annotated with @DataBaseTable");
        }SqlStatementBuilder sqlStatementBuilder = new SqlStatementBuilder();
        dbConnection.setAutoCommit(false);
        int counter = 0;
        for (Entity entity : entities) {
            if (insert(entity, sqlStatementBuilder, dbConnection)) {
                counter++;
            }
        }
        if (counter != entities.size()) {
            dbConnection.rollback();
            return 0;
        }
        return counter;
    }

    private boolean insert(Object entity, SqlStatementBuilder sqlStatementBuilder, Connection connection)
            throws SQLException {
        PreparedStatement statement = sqlStatementBuilder.insertStatement(entity, connection);
        int insertedRows = statement.executeUpdate();

        if (insertedRows < 1) {
            connection.rollback();
            return false;
        }
        Object primaryKeyValue = getPrimaryKeyValue(statement);
        var primaryKeyName = entity.getClass().getAnnotation(DataBaseTable.class).primaryKeyColumn();
        Field primaryKeyField = EntityHelpers.findField(entity, primaryKeyName);
        setPrimaryKey(primaryKeyValue, entity, primaryKeyField);
        insertRelationsOf(entity, connection, primaryKeyValue,
                primaryKeyField.getType());
        return true;
    }

    private void insertRelationsOf(Object entity, Connection connection, Object primaryKeyValue,
            Class<?> primaryKeyClass) throws SQLException {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(primaryKeyValue);
        List<ConcreteRelation> relations = EntityHelpers.findRelations(entity);
        var statementBuilder = new SqlStatementBuilder();
        for (ConcreteRelation relation : relations) {
            Class<?> fieldType = relation.valueType();
            var objectValue = relation.value();
            if (objectValue == null) {
                continue;
            }
            if (Collection.class.isAssignableFrom(fieldType)) {
                Collection<?> value = (Collection<?>) objectValue;
                for (Object object : value) {
                    EntityHelpers.setValueOf(EntityHelpers.findField(object, relation.exportedForeignKeyName()), object,
                            primaryKeyValue);
                    insert(object, statementBuilder, connection);
                }
            } else {
                var object = objectValue;
                EntityHelpers.setValueOf(EntityHelpers.findField(object, relation.exportedForeignKeyName()), object,
                        primaryKeyValue);
                insert(object, statementBuilder, connection);
            }
        }
    }

    private Object getPrimaryKeyValue(PreparedStatement statement) throws SQLException {
        try (ResultSet resultSet = statement.getGeneratedKeys()) {
            if (resultSet.next()) {
                return resultSet.getObject(1);
            } else {
                throw new IllegalStateException("PreparedStatement doesn't have generated keys");
            }
        }
    }

    private void setPrimaryKey(Object primaryKeyValue, Object entity, Field primaryKeyField) throws SQLException {
        Class<?> fieldType = primaryKeyField.getType();
        if (String.class.isAssignableFrom(fieldType) && (primaryKeyValue instanceof String keyString)) {
            EntityHelpers.setValueOf(primaryKeyField, entity, keyString);
        } else if (Integer.class.isAssignableFrom(fieldType) && (primaryKeyValue instanceof Integer keyInteger)) {
            EntityHelpers.setValueOf(primaryKeyField, entity, keyInteger);
        } else if (Long.class.isAssignableFrom(fieldType) && (primaryKeyValue instanceof Long keyLong)) {
            EntityHelpers.setValueOf(primaryKeyField, entity, keyLong);
        } else {
            throw new UnsupportedTypeException().fieldName(primaryKeyField.getName()).type(fieldType);
        }

    }
}
