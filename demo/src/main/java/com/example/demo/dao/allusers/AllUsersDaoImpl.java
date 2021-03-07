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

    @Override
    public User getUserByUsername(String username) {
        return loginStorage.get(username);
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return loginStorage.containsKey(username);
    }

    @Override
    public User addUser(String username, String password) {
        if (checkUsernameExists(username)) {
            return null;
        }

        var user = new User(username, 0L);
        loginStorage.put(username, user);
        passwordStorage.put(username, password);

        return user;
    }

    @Override
    public boolean checkPassword(String username, String password) {
        return Objects.equals(passwordStorage.get(username), password);
    }

    @Override
    public Map<String, String> getAll() {
        return passwordStorage;
    }
}
