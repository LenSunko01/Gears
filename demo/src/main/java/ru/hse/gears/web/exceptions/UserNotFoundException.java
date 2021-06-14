package ru.hse.gears.web.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("Could not find user " + id);
    }
    public UserNotFoundException() {
        super("Could not find user");
    }
    public UserNotFoundException(Exception e) {
        super("Could not find user", e);
    }
}