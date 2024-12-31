package com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.concretebuilders;

import java.util.List;

import com.eldoheiri.datastore.exceptions.BuilderMultipleUseException;
import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.attributes.PredicateValue;
import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.buildersinterfaces.IPredicate;

final class CompletePredicate implements IPredicate {
    private BuilderState state;

    private boolean isConsumed = false;

    CompletePredicate(BuilderState state) {
        this.state = state;
    }

    @Override
    public String sqlPredicate() {
        return state.queryBuilder().toString();
    }

    @Override
    public IPredicate and(IPredicate predicate) {
        if (isConsumed) {
            throw new BuilderMultipleUseException();
        }
        isConsumed = true;
        state.queryBuilder().append(" AND " + predicate.sqlPredicate().trim());
        state.predicateValues().addAll(predicate.predicateValues());
        return new CompletePredicate(state);
    }

    @Override
    public IPredicate or(IPredicate predicate) {
        if (isConsumed) {
            throw new BuilderMultipleUseException();
        }
        isConsumed = true;
        state.queryBuilder().append(" OR " + predicate.sqlPredicate().trim());
        state.predicateValues().addAll(predicate.predicateValues());
        return new CompletePredicate(state);
    }

    @Override
    public IPredicate encloseInParenthesis() {
        if (isConsumed) {
            throw new BuilderMultipleUseException();
        }
        isConsumed = true;
        StringBuilder newStringBuilder = new StringBuilder();
        newStringBuilder.append("(" + state.queryBuilder().toString().trim() + ")");
        return new CompletePredicate(new BuilderState(newStringBuilder, state.predicateValues()));
    }

    @Override
    public List<PredicateValue> predicateValues() {
        return state.predicateValues();
    }
}
