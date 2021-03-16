package com.example.demo.dao.allusers;

import com.example.demo.models.dto.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Repository
public class AllUsersDaoImpl implements AllUsersDao {

    Map<String, User> loginStorage = new HashMap<>();
    Map<String, String> passwordStorage = new HashMap<>();
    Map<String, User> tokenStorage = new HashMap<>();

    @Override
    public User getUserByUsername(String username) {
        return loginStorage.get(username);
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return loginStorage.containsKey(username);
    }

    @Override
    public boolean checkTokenExists(String token) {
        return tokenStorage.containsKey(token);
    }

    @Override
    public User addUser(String username, String password, String token) {
        if (checkUsernameExists(username)) {
            return null;
        }

        if (checkTokenExists(token)) {
            return null;
        }

        var user = new User(username, 0L);
        loginStorage.put(username, user);
        passwordStorage.put(username, password);
        tokenStorage.put(token, user);

        return user;
    }

    @Override
    public boolean checkPassword(String username, String password) {
        return Objects.equals(passwordStorage.get(username), password);
    }

    @Override
    public Map<String, String> getAllUsersInfo() {
        return passwordStorage;
    }

    @Override
    public Map<String, User> getUsersTokens() {
        return tokenStorage;
    }

    @Override
    public boolean removeToken(String token) {
        if (!checkTokenExists(token)) {
            return false;
        }
        tokenStorage.remove(token);
        return true;
    }

    @Override
    public boolean addToken(String token, User user) {
        if (checkTokenExists(token)) {
            return false;
        }
        tokenStorage.put(token, user);
        return true;
    }
}
