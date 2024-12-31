package com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.concretebuilders;

import com.eldoheiri.datastore.exceptions.BuilderMultipleUseException;
import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.attributes.AttributeType;
import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.attributes.PredicateValue;
import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.buildersinterfaces.IPredicate;
import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.buildersinterfaces.IValueBuilder;

final class ValueBuilder implements IValueBuilder {
    private final BuilderState state;

    private boolean isConsumed = false;

    ValueBuilder(BuilderState state) {
        this.state = state;
    }

    @Override
    public IPredicate expression(String expression, Object...values) {
        if (isConsumed) {
            throw new BuilderMultipleUseException();
        }
        isConsumed = true;
        state.queryBuilder().append(" " + expression.trim());
        for (Object value : values) {
            if (value instanceof Integer integer) {
                state.predicateValues().add(new PredicateValue(integer, AttributeType.INT));
            } else if (value instanceof String string) {
                state.predicateValues().add(new PredicateValue(string, AttributeType.TEXT));
            } else {
                throw new ClassCastException("Only String or Integer values are allowed");
            }
        }
        return new CompletePredicate(state);
    }

    @Override
    public IPredicate integer(int value) {
        if (isConsumed) {
            throw new BuilderMultipleUseException();
        }
        isConsumed = true;
        state.queryBuilder().append(" ?");
        state.predicateValues().add(new PredicateValue(value, AttributeType.INT));
        return new CompletePredicate(state);
    }

    @Override
    public IPredicate string(String value) {
        if (isConsumed) {
            throw new BuilderMultipleUseException();
        }
        isConsumed = true;
        state.queryBuilder().append(" ?");
        state.predicateValues().add(new PredicateValue(value, AttributeType.TEXT));
        return new CompletePredicate(state);
    }

    @Override
    public IPredicate object(Object value) {
        if (value instanceof String string) {
            return string(string);
        } else if (value instanceof Integer integer) {
            return integer(integer);
        } else {
            throw new ClassCastException("Only String or Integer values are allowed");
        }
    }
}
