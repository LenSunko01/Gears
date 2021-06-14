package ru.hse.gears.service.login;

import ru.hse.gears.models.dto.User;

public interface LoginService {
    User.UserInformation loginUser(String username, String password);
}
