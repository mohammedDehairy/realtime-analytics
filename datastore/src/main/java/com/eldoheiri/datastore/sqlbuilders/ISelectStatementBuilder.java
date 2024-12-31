package com.eldoheiri.datastore.sqlbuilders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.buildersinterfaces.IPredicate;

public interface ISelectStatementBuilder {
    PreparedStatement selectStatement(IPredicate predicate, Class<?> entityClass, Connection connection) throws SQLException;
}
