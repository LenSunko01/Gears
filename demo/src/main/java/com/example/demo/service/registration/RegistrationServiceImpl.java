package com.example.demo.service.registration;

import com.example.demo.dao.allusers.AllUsersDao;
import com.example.demo.service.token.TokenService;
import com.example.demo.web.exceptions.InvalidUsernameException;
import com.example.demo.web.exceptions.InvalidPasswordException;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.Map;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    private final AllUsersDao allUsers;
    private final TokenService tokenService;

    public RegistrationServiceImpl(
            AllUsersDao allUsers,
            TokenService tokenService
    ) {
        this.allUsers = allUsers;
        this.tokenService = tokenService;
    }

    @Override
    public Map.Entry<String, Long> registerUser(String username, String password) {
        checkLoginIsValid(username);
        checkPasswordIsValid(password);

        var token = tokenService.generateNewToken();
        var user = allUsers.addUser(username, password, token);
        return new AbstractMap.SimpleEntry<>(token, allUsers.getIdByUsername(username));
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

        int lower = 0;
        int upper = 0;
        int digits = 0;

        for (int i = 0; i < password.length(); i++) {
            if (Character.isDigit(password.charAt(i))) {
                digits++;
            }
            if (Character.isLowerCase(password.charAt(i))) {
                lower++;
            }
            if (Character.isUpperCase(password.charAt(i))) {
                upper++;
            }
        }

        if (lower == 0) {
            throw new InvalidPasswordException("Password must contain characters in lower case");
        }

        if (upper == 0) {
            throw new InvalidPasswordException("Password must contain characters in upper case");
        }

        if (digits == 0) {
            throw new InvalidPasswordException("Password must contain digits");
        }
    }
}
