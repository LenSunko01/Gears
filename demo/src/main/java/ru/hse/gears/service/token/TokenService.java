package ru.hse.gears.service.token;

public interface TokenService {
    boolean checkTokenExists(String token);
    String generateNewToken();
}
