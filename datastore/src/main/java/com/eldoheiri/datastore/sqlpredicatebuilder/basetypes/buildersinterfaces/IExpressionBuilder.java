package com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.buildersinterfaces;

public interface IExpressionBuilder {
    IPredicate expression(String expression, Object...values);  
}
