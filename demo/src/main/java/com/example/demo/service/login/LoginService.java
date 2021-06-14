package com.example.demo.service.login;

import com.example.demo.models.dto.User;

public interface LoginService {
    User.UserInformation loginUser(String username, String password);
}
