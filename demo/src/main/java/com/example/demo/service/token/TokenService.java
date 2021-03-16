package com.example.demo.service.token;

public interface TokenService {
    boolean checkTokenExists(String token);

    String generateNewToken();
}
