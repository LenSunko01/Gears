package com.example.demo.dao.activeusers;

import com.example.demo.models.dto.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ActiveUsersDaoImpl implements ActiveUsersDao {

    Map<String, User> tokenStorage = new HashMap<>();

    @Override
    public User getUserByToken(String token) {
        return tokenStorage.get(token);
    }

    @Override
    public boolean checkTokenExists(String token) {
        return tokenStorage.containsKey(token);
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
    public boolean addUser(User user, String token) {
        if (checkTokenExists(token)) {
            return false;
        }
        tokenStorage.put(token, user);
        return true;
    }

    @Override
    public Map<String, User> getActiveUsers() {
        return tokenStorage;
    }
}