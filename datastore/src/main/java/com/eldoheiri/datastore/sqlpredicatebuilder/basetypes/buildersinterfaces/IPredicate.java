package com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.buildersinterfaces;

import java.util.List;

import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.attributes.PredicateValue;

public interface IPredicate {
    String sqlPredicate();

    List<PredicateValue> predicateValues();

    IPredicate and(IPredicate predicate);

    IPredicate or(IPredicate predicate);

    IPredicate encloseInParenthesis();
}
