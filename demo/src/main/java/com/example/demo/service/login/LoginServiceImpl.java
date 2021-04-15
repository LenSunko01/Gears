package com.example.demo.service.login;

import com.example.demo.dao.allusers.AllUsersDao;
import com.example.demo.service.token.TokenService;
import com.example.demo.web.exceptions.InvalidUsernameException;
import com.example.demo.web.exceptions.InvalidPasswordException;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
    private final AllUsersDao allUsers;

    public LoginServiceImpl(AllUsersDao allUsers) {
        this.allUsers = allUsers;
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

        return allUsers.getTokenByUsername(username);
    }
}
