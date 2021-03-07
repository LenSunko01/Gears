package com.example.demo.service.login;

import com.example.demo.dao.activeusers.ActiveUsersDao;
import com.example.demo.dao.activeusers.ActiveUsersDaoImpl;
import com.example.demo.dao.allusers.AllUsersDao;
import com.example.demo.dao.allusers.AllUsersDaoImpl;
import com.example.demo.service.token.TokenService;
import com.example.demo.service.token.TokenServiceImpl;
import com.example.demo.web.exceptions.InvalidUsernameException;
import com.example.demo.web.exceptions.InvalidPasswordException;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
    private final AllUsersDao allUsers;
    private final ActiveUsersDao activeUsers;
    private final TokenService tokenService;

    public LoginServiceImpl(
            AllUsersDao allUsers,
            ActiveUsersDao activeUsers,
            TokenService tokenService
    ) {
        this.allUsers = allUsers;
        this.activeUsers = activeUsers;
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
        activeUsers.addUser(user, token);

        return token;
    }

    @Override
    public void logoutUser(String token) {
        activeUsers.removeToken(token);
    }
}
