package com.example.demo.service.token;

import com.example.demo.dao.allusers.AllUsersDao;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class TokenServiceImpl implements TokenService {
    private final AllUsersDao allUsers;

    public TokenServiceImpl(AllUsersDao allUsers) {
        this.allUsers = allUsers;
    }

    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe


    @Override
    public boolean checkTokenExists(String token) {
        return allUsers.checkTokenExists(token);
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
        return allUsers.removeToken(token);
    }
}
