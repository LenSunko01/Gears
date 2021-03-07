package com.example.demo.service.token;

import com.example.demo.dao.activeusers.ActiveUsersDao;
import com.example.demo.dao.activeusers.ActiveUsersDaoImpl;
import com.example.demo.service.token.TokenService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class TokenServiceImpl implements TokenService {
    private final ActiveUsersDao activeUsers;

    public TokenServiceImpl(ActiveUsersDao activeUsers) {
        this.activeUsers = activeUsers;
    }

    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe


    @Override
    public boolean checkTokenExists(String token) {
        return activeUsers.checkTokenExists(token);
    }

    @Override
    public String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        var token = base64Encoder.encodeToString(randomBytes);
        while (checkTokenExists(token)) {
            secureRandom.nextBytes(randomBytes);
            token = base64Encoder.encodeToString(randomBytes);
        }
        return token;
    }

    @Override
    public boolean deleteToken(String token) {
        return activeUsers.removeToken(token);
    }
}
