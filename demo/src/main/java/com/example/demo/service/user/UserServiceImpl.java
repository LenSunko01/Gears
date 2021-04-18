package com.example.demo.service.user;

import com.example.demo.dao.allusers.AllUsersDao;
import com.example.demo.models.dto.User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final AllUsersDao allUsers;

    public UserServiceImpl(AllUsersDao allUsers) {
        this.allUsers = allUsers;
    }

    @Override
    public User getUserById(Long id) {
        return allUsers.getUserById(id);
    }

    @Override
    public Map.Entry<String, Long> getRandomUser() {
        Map<String, Long> map = allUsers.getAll();
        var list = new ArrayList<>(map.entrySet());
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    @Override
    public Map<String, Long> getAll() {
        return allUsers.getAll();
    }

    @Override
    public User updateUsername(Long id, String newUsername) {
        return allUsers.updateUsernameById(id, newUsername);
    }

    @Override
    public User updatePassword(Long id, String newPassword) {
        return allUsers.updatePasswordById(id, newPassword);
    }

    @Override
    public User updatePoints(Long id, Long newPoints) {
        return allUsers.updatePointsById(id, newPoints);
    }

    @Override
    public User updateUsername(String username, String newUsername) {
        var id = allUsers.getUserByUsername(username).getId();
        return allUsers.updateUsernameById(id, newUsername);
    }

    @Override
    public User updatePassword(String username, String newPassword) {
        var id = allUsers.getUserByUsername(username).getId();
        return allUsers.updatePasswordById(id, newPassword);
    }

    @Override
    public User updatePoints(String username, Long newPoints) {
        var id = allUsers.getUserByUsername(username).getId();
        return allUsers.updatePointsById(id, newPoints);
    }
}
