package com.eldoheiri.datastore;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import com.eldoheiri.datastore.factories.DefaultEntityFactory;
import com.eldoheiri.datastore.factories.EntityFactory;
import com.eldoheiri.datastore.implementations.deletion.DataStoreDeletor;
import com.eldoheiri.datastore.implementations.insertion.DataStoreInsertor;
import com.eldoheiri.datastore.implementations.insertion.IDataStoreInsertor;
import com.eldoheiri.datastore.implementations.selection.DataStoreSelector;
import com.eldoheiri.datastore.implementations.selection.IDataStoreSelector;
import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.buildersinterfaces.IPredicate;

public class DataStore implements IDataStoreInsertor, IDataStoreSelector {

    private final EntityFactory factory;

    public DataStore() throws SQLException {
        this(new DefaultEntityFactory());
    }

    public DataStore(EntityFactory factory) {
        Objects.requireNonNull(factory);
        this.factory = factory;
    }

    @Override
    public <Entity> boolean insert(Entity entity, Connection dbConnection) throws SQLException {
        Objects.requireNonNull(entity);
        return new DataStoreInsertor().insert(entity, dbConnection);
    }

    @Override
    public <Entity> int insert(List<Entity> entities, Connection dbConnection) throws SQLException {
        Objects.requireNonNull(entities);
        if (entities.isEmpty()) {
            return 0;
        }
        return new DataStoreInsertor().insert(entities, dbConnection);
    }

    public <Entity> boolean update(Entity entity) {
        return true;
    }

    public <Entity> int delete(IPredicate predicate, Class<Entity> entityClass, Connection dbConnection, boolean cascade) throws SQLException {
        return new DataStoreDeletor().delete(predicate, entityClass, dbConnection, cascade);
    }

    public <Entity> int delete(IPredicate predicate, Class<Entity> entityClass, Connection dbConnection) throws SQLException {
        return delete(predicate, entityClass, dbConnection, false);
    }

    @Override
    public <Entity> List<Entity> get(IPredicate predicate, Class<Entity> entityClass, Connection dbConnection) throws SQLException {
        return new DataStoreSelector(factory).get(predicate, entityClass, dbConnection);
    }
}
