package com.example.demo.service.login;

import com.example.demo.dao.allusers.AllUsersDao;
import com.example.demo.service.token.TokenService;
import com.example.demo.web.exceptions.InvalidUsernameException;
import com.example.demo.web.exceptions.InvalidPasswordException;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
    private final AllUsersDao allUsers;
    private final TokenService tokenService;

    public LoginServiceImpl(
            AllUsersDao allUsers,
            TokenService tokenService
    ) {
        this.allUsers = allUsers;
        this.tokenService = tokenService;
    }

    @Override
    public String loginUser(String username, String password) {
        if (!allUsers.checkUsernameExists(username)) {
            throw new InvalidUsernameException("Could not find the login. " +
                    "I think you made a typo. Or you're just messing with me (please don't)");
        }

        if (!allUsers.checkPassword(username, password)) {
            throw new InvalidPasswordException("Incorrect password");
        }

        var token = tokenService.generateNewToken();
        var user = allUsers.getUserByUsername(username);
        allUsers.addToken(token, user);

        return token;
    }

    @Override
    public void logoutUser(String token) {
        allUsers.removeToken(token);
    }
}
