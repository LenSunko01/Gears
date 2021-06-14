package ru.hse.gears.service.login;

import ru.hse.gears.dao.users.AllUsersDao;
import ru.hse.gears.models.dto.User;
import ru.hse.gears.service.token.TokenService;
import ru.hse.gears.web.exceptions.InvalidUsernameException;
import ru.hse.gears.web.exceptions.InvalidPasswordException;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
    private final AllUsersDao allUsers;
    private final TokenService tokenService;

    public LoginServiceImpl(AllUsersDao allUsers, TokenService tokenService) {
        this.allUsers = allUsers;
        this.tokenService = tokenService;
    }

    @Override
    public User.UserInformation loginUser(String username, String password) {
        if (!allUsers.checkUsernameExists(username)) {
            throw new InvalidUsernameException("Could not find the login. " +
                    "I think you made a typo. Or you're just messing with me (please don't)");
        }

        if (!allUsers.checkPasswordIsCorrect(username, password)) {
            throw new InvalidPasswordException("Incorrect password");
        }

        var token = tokenService.generateNewToken();
        allUsers.updateToken(token, username);
        return new User.UserInformation(token, allUsers.getIdByUsername(username));
    }
}
