package com.example.demo.web.controllers.advices;

import com.example.demo.web.exceptions.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class AuthenticationAdvice {

    @ResponseBody
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String authenticationHandler(AuthenticationException ex) {
        return ex.getMessage();
    }
}