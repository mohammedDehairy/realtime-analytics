package com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.concretebuilders;

import com.eldoheiri.datastore.exceptions.BuilderMultipleUseException;
import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.buildersinterfaces.IAttributeBuilder;
import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.buildersinterfaces.IOperatorBuilder;

final class AttributeBuilder implements IAttributeBuilder {
    private BuilderState state;

    private boolean isConsumed = false;

    AttributeBuilder(BuilderState state) {
        this.state = state;
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
