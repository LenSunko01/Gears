package com.example.demo.service.login;

public interface LoginService {
    String loginUser(String username, String password);

    void logoutUser(String token);
}
