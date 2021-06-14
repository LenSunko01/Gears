package ru.hse.gears.service.registration;

import ru.hse.gears.models.dto.User;

public interface RegistrationService {
    /*
        Throws InvalidUsernameException if username is not valid
        Throws InvalidPasswordException if password is incorrect
    */
    User.UserInformation registerUser(String username, String password);

    /*
        Throws InvalidUsernameException if username is an empty string or if it is already taken
    */
    void checkLoginIsValid(String username);

    /*
        Throws InvalidPasswordException if password is invalid
        Valid password must contain digits, characters in lower and upper case
    */
    void checkPasswordIsValid(String password);
}
