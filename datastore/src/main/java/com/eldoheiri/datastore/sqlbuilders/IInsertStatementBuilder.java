package com.eldoheiri.datastore.sqlbuilders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface IInsertStatementBuilder {
    PreparedStatement insertStatement(Object entity, Connection connection) throws SQLException;
}
