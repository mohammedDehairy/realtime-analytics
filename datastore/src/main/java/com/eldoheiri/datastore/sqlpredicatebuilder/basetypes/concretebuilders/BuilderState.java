package com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.concretebuilders;

import java.util.List;
import java.util.Objects;

import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.attributes.PredicateValue;

record BuilderState(StringBuilder queryBuilder, List<PredicateValue> predicateValues) {
    BuilderState {
        Objects.requireNonNull(queryBuilder);
        Objects.requireNonNull(predicateValues);
    }
}
