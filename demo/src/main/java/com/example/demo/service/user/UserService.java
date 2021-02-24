package com.example.demo.service.user;

import com.example.demo.models.dto.User;

import java.util.List;

public interface UserService {
    User getUserById(Long id);
    List<User> getAll();
    void updateUser(Long id, User user);
    void deleteUserById(Long id);
}
