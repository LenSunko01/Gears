package ru.hse.gears.service.token;

public interface TokenService {
    /*
        Returns true if token exists and false otherwise
    */
    boolean checkTokenExists(String token);

    /*
        Generates new token and returns it
    */
    String generateNewToken();
}
