package com.example.demo.dao.allusers;

import com.example.demo.models.dto.User;

import java.util.List;
import java.util.Map;

public interface AllUsersDao {
    User getUserByUsername(String username);

    boolean checkUsernameExists(String username);

    User addUser(String username, String password, String token);

    boolean checkPassword(String username, String password);

    Map<String, String> getAllUsersInfo();

    Map<String, User> getUsersTokens();

    boolean checkTokenExists(String token);

    boolean removeToken(String token);

    boolean addToken(String token, User user);
}
