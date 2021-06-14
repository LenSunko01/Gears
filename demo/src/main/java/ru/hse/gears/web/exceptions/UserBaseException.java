package ru.hse.gears.web.exceptions;

public class UserBaseException extends RuntimeException {
    public UserBaseException() {
        super("Can not correctly work with data base");
    }
    public UserBaseException(Exception e) {
        super("Can not correctly work with data base", e);
    }
}
