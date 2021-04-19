package com.example.demo.service.login;

import com.example.demo.dao.allusers.AllUsersDao;
import com.example.demo.service.token.TokenService;
import com.example.demo.web.exceptions.InvalidUsernameException;
import com.example.demo.web.exceptions.InvalidPasswordException;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {
    private final AllUsersDao allUsers;
    private final TokenService tokenService;

    public LoginServiceImpl(AllUsersDao allUsers, TokenService tokenService) {
        this.allUsers = allUsers;
        this.tokenService = tokenService;
    }

    @Override
    public Map.Entry<String, Long> loginUser(String username, String password) {
        if (!allUsers.checkUsernameExists(username)) {
            throw new InvalidUsernameException("Could not find the login. " +
                    "I think you made a typo. Or you're just messing with me (please don't)");
        }

        if (!allUsers.checkPasswordIsCorrect(username, password)) {
            throw new InvalidPasswordException("Incorrect password");
        }

        var token = tokenService.generateNewToken();
        allUsers.updateToken(token, username);
        return new AbstractMap.SimpleEntry<>(token, allUsers.getIdByUsername(username));
    }
}
