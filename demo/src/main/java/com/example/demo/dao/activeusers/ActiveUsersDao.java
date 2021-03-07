package com.example.demo.dao.activeusers;

import com.example.demo.models.dto.User;

import java.util.Map;

public interface ActiveUsersDao {
    User getUserByToken(String token);

    boolean checkTokenExists(String token);

    boolean removeToken(String token);

    boolean addUser(User user, String token);

    Map<String, User> getActiveUsers();
}
