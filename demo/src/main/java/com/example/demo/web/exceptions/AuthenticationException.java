package com.example.demo.web.exceptions;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException() {
        super("No right to access this resource");
    }
}

