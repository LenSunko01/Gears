package com.example.demo.web.exceptions;

public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException(String exc) {
        super(exc);
    }
}

