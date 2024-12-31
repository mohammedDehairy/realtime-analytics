package com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.buildersinterfaces;

public interface IValueBuilder extends IExpressionBuilder {
    IPredicate integer(int value);

    IPredicate string(String value);

    IPredicate object(Object value);
}
