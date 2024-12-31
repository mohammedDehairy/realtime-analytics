package com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.concretebuilders;

import java.util.ArrayList;

import com.eldoheiri.datastore.exceptions.BuilderMultipleUseException;
import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.buildersinterfaces.IAttributeBuilder;
import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.buildersinterfaces.IOperatorBuilder;

public final class SQLPredicateBuilder implements IAttributeBuilder {
    private final BuilderState state;

    private boolean isConsumed = false;

    private SQLPredicateBuilder(BuilderState state) {
        this.state = state;
    }

    public static IAttributeBuilder newPredicate() {
        return new SQLPredicateBuilder(new BuilderState(new StringBuilder(), new ArrayList<>()));
    }

    @Override
    public IOperatorBuilder attribute(String name) {
        if (isConsumed) {
            throw new BuilderMultipleUseException();
        }
        isConsumed = true;
        state.queryBuilder().append(" " + name);
        return new OperatorBuilder(state);
    }
}
