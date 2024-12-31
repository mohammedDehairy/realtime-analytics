package com.eldoheiri.datastore.sqlbuilders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.buildersinterfaces.IPredicate;

public interface IDeleteStatementBuilder {
    PreparedStatement deleteStatement(IPredicate predicate, Class<?> entityClass, Connection connection, boolean cascade) throws SQLException;
}
