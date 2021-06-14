package ru.hse.gears.service.registration;

import ru.hse.gears.models.dto.User;

public interface RegistrationService {
    User.UserInformation registerUser(String username, String password);
    void checkLoginIsValid(String username);
    void checkPasswordIsValid(String password);
}
