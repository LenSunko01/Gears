package ru.hse.gears.service.token;

import ru.hse.gears.dao.user.UserDao;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class TokenServiceImpl implements TokenService {
    private final UserDao allUsers;

    public TokenServiceImpl(UserDao allUsers) {
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
}
