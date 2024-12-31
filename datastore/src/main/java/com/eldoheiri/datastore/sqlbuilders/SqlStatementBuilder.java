package com.eldoheiri.datastore.sqlbuilders;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.buildersinterfaces.IPredicate;

import java.sql.Connection;

public final class SqlStatementBuilder implements IInsertStatementBuilder, ISelectStatementBuilder {

    public PreparedStatement deleteStatement(IPredicate predicate, Class<?> entityClass, Connection connection, boolean cascade) throws SQLException {
        return new DeleteStatementBuilder().deleteStatement(predicate, entityClass, connection, cascade);
    }

    public PreparedStatement insertStatement(Object entity,
            Connection connection) throws SQLException {
        return new InsertStatementBuilder().insertStatement(entity, connection);
    }

    public PreparedStatement selectStatement(IPredicate predicate, Class<?> entityClass, Connection connection) throws SQLException {
        return new SelectStatementBuilder().selectStatement(predicate, entityClass, connection);
    }
}
