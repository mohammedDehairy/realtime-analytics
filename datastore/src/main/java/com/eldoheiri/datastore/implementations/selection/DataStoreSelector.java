package com.eldoheiri.datastore.implementations.selection;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import com.eldoheiri.datastore.annotations.DataBaseTable;
import com.eldoheiri.datastore.factories.DefaultEntityFactory;
import com.eldoheiri.datastore.factories.EntityFactory;
import com.eldoheiri.datastore.helpers.EntityHelpers;
import com.eldoheiri.datastore.helpers.SchemaRelation;
import com.eldoheiri.datastore.sqlbuilders.SqlStatementBuilder;
import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.buildersinterfaces.IPredicate;
import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.concretebuilders.SQLPredicateBuilder;

public class DataStoreSelector {

    private final EntityFactory factory;

    public DataStoreSelector() {
        this(new DefaultEntityFactory());
    }

    public DataStoreSelector(EntityFactory factory) {
        Objects.requireNonNull(factory);
        this.factory = factory;
    }

    public <Entity> List<Entity> get(IPredicate predicate, Class<Entity> entityClass, Connection dbConnection) throws SQLException {
        SqlStatementBuilder sqlStatementBuilder = new SqlStatementBuilder();
        dbConnection.setAutoCommit(false);
        List<Entity> result = select(predicate, entityClass, dbConnection, sqlStatementBuilder);
        selectRelationsOf(result, entityClass, dbConnection, sqlStatementBuilder);
        dbConnection.commit();
        return result;
    }

    private <Entity> void selectRelationsOf(List<Entity> result, Class<Entity> entityClass, Connection connection,
            SqlStatementBuilder sqlStatementBuilder) throws SQLException {
        List<SchemaRelation> relations = EntityHelpers.findSchemaRelations(entityClass);
        for (Entity entity : result) {
            for (SchemaRelation relation : relations) {
                Field field = relation.field();
                if (field.getType().isAssignableFrom(List.class)) {
                    ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                    Class<?> relatedEntityClass = (Class<?>) genericType.getActualTypeArguments()[0];
                    String primaryKeyName = entity.getClass().getAnnotation(DataBaseTable.class).primaryKeyColumn();
                    Object primaryKeyValue = EntityHelpers.getValueOf(EntityHelpers.findField(entity, primaryKeyName),
                            entityClass, Object.class);
                    var relationPredicate = SQLPredicateBuilder.newPredicate().attribute(relation.exportedForeignKeyName()).equals().object(primaryKeyValue);
                    List<?> relatedObjects = select(relationPredicate,
                            relatedEntityClass, connection, sqlStatementBuilder);
                    EntityHelpers.setValueOf(field, entityClass, relatedObjects);
                } else {
                    Class<?> relatedEntityClass = field.getType();
                    String primaryKeyName = entity.getClass().getAnnotation(DataBaseTable.class).primaryKeyColumn();
                    Object primaryKeyValue = EntityHelpers.getValueOf(EntityHelpers.findField(entity, primaryKeyName),
                            entityClass, Object.class);
                    
                    var relationPredicate = SQLPredicateBuilder.newPredicate().attribute(relation.exportedForeignKeyName()).equals().object(primaryKeyValue);
                    List<?> relatedObjects = select(relationPredicate,
                            relatedEntityClass, connection, sqlStatementBuilder);
                    if (relatedObjects.isEmpty()) {
                        continue;
                    } else if (relatedObjects.size() == 1) {
                        EntityHelpers.setValueOf(field, entityClass, relatedObjects.get(0));
                    } else {
                        throw new IllegalStateException("Relation " + field.getName() + "of class " + entityClass
                                + " shouldn't have more than one instance in the database. consider changing the relation to a List instead.");
                    }
                }
            }
        }
    }

    private <Entity> List<Entity> select(IPredicate predicate, Class<Entity> entityClass, Connection connection,
            SqlStatementBuilder sqlStatementBuilder) throws SQLException {
        PreparedStatement statement = sqlStatementBuilder.selectStatement(predicate, entityClass, connection);
        ResultSet resultSet = statement.executeQuery();
        return factory.fromResultSet(resultSet, entityClass);
    }
}
