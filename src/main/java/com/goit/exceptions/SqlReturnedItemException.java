package com.goit.exceptions;

public class SqlReturnedItemException extends RuntimeException {
    public SqlReturnedItemException(String message) {
        super(message);
    }
}