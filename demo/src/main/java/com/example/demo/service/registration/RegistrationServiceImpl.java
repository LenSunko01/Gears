package com.example.demo.service.registration;

import com.example.demo.dao.activeusers.ActiveUsersDao;
import com.example.demo.dao.allusers.AllUsersDao;
import com.example.demo.models.dto.User;
import com.example.demo.service.token.TokenService;
import com.example.demo.web.exceptions.InvalidUsernameException;
import com.example.demo.web.exceptions.InvalidPasswordException;
import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    private final AllUsersDao allUsers;
    private final ActiveUsersDao activeUsers;
    private final TokenService tokenService;

    public RegistrationServiceImpl(
            AllUsersDao allUsers,
            ActiveUsersDao activeUsers,
            TokenService tokenService
    ) {
        this.allUsers = allUsers;
        this.activeUsers = activeUsers;
        this.tokenService = tokenService;
    }

    @Override
    public String registerUser(String username, String password) {
        checkLoginIsValid(username);
        checkPasswordIsValid(password);

        var user = allUsers.addUser(username, password);
        var token = tokenService.generateNewToken();
        activeUsers.addUser(user, token);

        return token;
    }

    private void checkLoginIsValid(String username) {
        if (username.length() == 0) {
            throw new InvalidUsernameException("Login can not be an empty string");
        }

        if (allUsers.checkUsernameExists(username)) {
            throw new InvalidUsernameException("Ooops, someone has already come up with that name...");
        }
    }

    private void checkPasswordIsValid(String password) {
        if (password.length() == 0) {
            throw new InvalidPasswordException("Seriously? Empty password? I thought better about you");
        }
    }
}
