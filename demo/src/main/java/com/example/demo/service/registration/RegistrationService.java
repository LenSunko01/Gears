package com.example.demo.service.registration;

import com.example.demo.models.dto.User;

import java.util.Map;

public interface RegistrationService {
    Map.Entry<String, Long> registerUser(String username, String password);
    void checkLoginIsValid(String username);
    void checkPasswordIsValid(String password);
}
