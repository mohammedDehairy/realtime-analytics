package com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.buildersinterfaces;

public interface IOperatorBuilder {
    IValueBuilder equals();

    IValueBuilder geaterThan();

    IValueBuilder lessThan();

    IValueBuilder geaterThanOrEquals();

    IValueBuilder lessThanOrEquals();

    IValueBuilder notEqual();
}
