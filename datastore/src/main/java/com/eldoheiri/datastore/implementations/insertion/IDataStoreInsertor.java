package com.eldoheiri.datastore.implementations.insertion;

import java.sql.SQLException;
import java.util.List;

import java.sql.Connection;

public interface IDataStoreInsertor {
    <Entity> boolean insert(Entity entity, Connection dbConnection) throws SQLException;

    <Entity> int insert(List<Entity> entities, Connection dbConnection) throws SQLException;
}
