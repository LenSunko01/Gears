package ru.hse.gears.web.exceptions;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String exc) {
        super(exc);
    }
}

