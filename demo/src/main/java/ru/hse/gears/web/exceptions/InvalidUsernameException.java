package ru.hse.gears.web.exceptions;

public class InvalidUsernameException extends RuntimeException {
    public InvalidUsernameException(String exc) {
        super(exc);
    }
}
