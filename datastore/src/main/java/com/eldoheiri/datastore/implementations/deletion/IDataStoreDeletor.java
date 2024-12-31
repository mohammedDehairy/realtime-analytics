package com.eldoheiri.datastore.implementations.deletion;

import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.buildersinterfaces.IPredicate;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDataStoreDeletor {
    <Entity> int delete(IPredicate predicate, Class<Entity> entityClass, Connection dbConnection, boolean cascade) throws SQLException;
}
