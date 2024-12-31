package com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.concretebuilders;

import com.eldoheiri.datastore.exceptions.BuilderMultipleUseException;
import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.buildersinterfaces.IOperatorBuilder;
import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.buildersinterfaces.IValueBuilder;

final class OperatorBuilder implements IOperatorBuilder {
    private final BuilderState state;

    private boolean isConsumed = false;

    OperatorBuilder(BuilderState state) {
        this.state = state;
    }

    private IValueBuilder operator(BooleanOperator operator) {
        if (isConsumed) {
            throw new BuilderMultipleUseException();
        }
        isConsumed = true;
        state.queryBuilder().append(" " + operator.getValue());
        return new ValueBuilder(state);
    }

    @Override
    public IValueBuilder equals() {
        return operator(BooleanOperator.EQUAL);
    }

    @Override
    public IValueBuilder geaterThan() {
        return operator(BooleanOperator.GREATER_THAN);
    }

    @Override
    public IValueBuilder lessThan() {
        return operator(BooleanOperator.LESS_THAN);
    }

    @Override
    public IValueBuilder geaterThanOrEquals() {
        return operator(BooleanOperator.GREAER_THAN_OR_EQUAL);
    }

    @Override
    public IValueBuilder lessThanOrEquals() {
        return operator(BooleanOperator.LESS_THAN_OR_EQUAL);
    }

    @Override
    public IValueBuilder notEqual() {
        return operator(BooleanOperator.NOT_EQUAL);
    }

    enum BooleanOperator {
        EQUAL("="),
        NOT_EQUAL("!="),
        GREATER_THAN(">"),
        LESS_THAN("<"),
        GREAER_THAN_OR_EQUAL(">="),
        LESS_THAN_OR_EQUAL("<=");
    
        private String value;
    
        BooleanOperator(String value) {
            this.value = value;
        }
    
        String getValue() {
            return this.value;
        }
    }
    
}
