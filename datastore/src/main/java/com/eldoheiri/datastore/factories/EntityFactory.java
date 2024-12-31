package com.eldoheiri.datastore.factories;

import java.sql.ResultSet;
import java.util.List;

public interface EntityFactory {
    public <Entity> List<Entity> fromResultSet(ResultSet resultSet, Class<Entity> clazz);
}
