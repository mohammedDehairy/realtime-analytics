package com.eldoheiri.datastore.exceptions;

public class BuilderMultipleUseException extends IllegalStateException {
    public BuilderMultipleUseException() {
        super("This Builder can only be consumed once");
    }
}
