package ru.hse.gears.service.login;

import ru.hse.gears.models.dto.User;

public interface LoginService {
    /*
        Throws InvalidUsernameException if username is not valid
        Throws InvalidPasswordException if password is incorrect
     */
    User.UserInformation loginUser(String username, String password);
}
