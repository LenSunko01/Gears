package com.example.demo.web.exceptions;

public class OpponentNotFoundException extends RuntimeException {

    public OpponentNotFoundException() {
        super("Could not find opponent to play, try again later");
    }
}
