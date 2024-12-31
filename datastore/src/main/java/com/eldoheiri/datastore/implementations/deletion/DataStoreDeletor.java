package com.eldoheiri.datastore.implementations.deletion;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import com.eldoheiri.datastore.annotations.DataBaseTable;
import com.eldoheiri.datastore.sqlbuilders.SqlStatementBuilder;
import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.buildersinterfaces.IPredicate;

public class DataStoreDeletor implements IDataStoreDeletor {

    @Override
    public <Entity> int delete(IPredicate predicate, Class<Entity> entityClass, Connection dbConnection, boolean cascade)
            throws SQLException {
        if (!entityClass.isAnnotationPresent(DataBaseTable.class)) {
            throw new IllegalArgumentException(
                    "Entity of type: " + entityClass + " must be annotated with @DataBaseTable");
        }
        PreparedStatement statement = new SqlStatementBuilder().deleteStatement(predicate, entityClass, dbConnection, cascade);
        return statement.executeUpdate();
    }
    
}
