package com.example.demo.web.exceptions;

public class SQLUserBaseException extends RuntimeException {
    public SQLUserBaseException() {
        super("Can not correctly work with data base");
    }
    public SQLUserBaseException(Exception e) {
        super("Can not correctly work with data base", e);
    }
}
