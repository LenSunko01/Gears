package com.example.demo.web.controllers.advices;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.example.demo.web.exceptions.InvalidPasswordException;

@ControllerAdvice
class InvalidPasswordAdvice {
    @ResponseBody
    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String userNotFoundHandler(InvalidPasswordException ex) {
        return ex.getMessage();
    }
}