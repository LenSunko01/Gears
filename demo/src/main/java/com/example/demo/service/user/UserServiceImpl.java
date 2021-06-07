package com.example.demo.service.user;

import com.example.demo.dao.allusers.AllUsersDao;
import com.example.demo.models.dto.User;
import com.example.demo.service.gamestate.GameStateService;
import com.example.demo.service.registration.RegistrationService;
import com.example.demo.web.controllers.UserController;
import com.example.demo.web.exceptions.AuthenticationException;
import com.example.demo.web.exceptions.OpponentNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserServiceImpl implements UserService {
    private static final Log logger = LogFactory.getLog(UserController.class);
    private final AllUsersDao allUsers;
    private final RegistrationService registrationService;
    private final GameStateService gameStateService;

    public UserServiceImpl(
            AllUsersDao allUsers,
            RegistrationService registrationService,
            GameStateService gameStateService) {
        this.allUsers = allUsers;
        this.registrationService = registrationService;
        this.gameStateService = gameStateService;
    }

    private void validateToken(Long id, String token) {
        var user = allUsers.getUserById(id);
        var username = user.getUsername();
        var correctToken = allUsers.getTokenByUsername(username);
        if (!correctToken.equals(token)) {
            throw new AuthenticationException();
        }
    }

    @Override
    public User getUserById(Long id, String token) {
        validateToken(id, token);
        return allUsers.getUserById(id);
    }

    @Override
    public Map.Entry<String, Long> getRandomUser() {
        Map<String, Long> map = allUsers.getAll();
        if (map.isEmpty()) {
            return null;
        }
        var list = new ArrayList<>(map.entrySet());
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    @Override
    public Map<String, Long> getAll() {
        return allUsers.getAll();
    }

    @Override
    public User updateUsername(Long id, String newUsername, String token) {
        validateToken(id, token);
        registrationService.checkLoginIsValid(newUsername);
        return allUsers.updateUsernameById(id, newUsername);
    }

    @Override
    public User updatePassword(Long id, String newPassword, String token) {
        validateToken(id, token);
        registrationService.checkPasswordIsValid(newPassword);
        return allUsers.updatePasswordById(id, newPassword);
    }

    @Override
    public User updatePoints(Long id, Long newPoints, String token) {
        validateToken(id, token);
        return allUsers.updatePointsById(id, newPoints);
    }

    @Override
    public User updatePicture(Long id, byte[] newPicture, String token) {
      //  validateToken(id, token);
        return allUsers.updatePicture(allUsers.getUserById(id).getUsername(), newPicture);
    }

    @Override
    public User getUserByUsername(String username, String token) {
        var user = allUsers.getUserByUsername(username);
        var id = user.getId();
        validateToken(id, token);
        return user;
    }

    @Override
    public User updateUsername(String username, String newUsername, String token) {
        var id = allUsers.getUserByUsername(username).getId();
        validateToken(id, token);
        registrationService.checkLoginIsValid(newUsername);
        return allUsers.updateUsernameById(id, newUsername);
    }

    @Override
    public User updatePassword(String username, String newPassword, String token) {
        var id = allUsers.getUserByUsername(username).getId();
        validateToken(id, token);
        registrationService.checkPasswordIsValid(newPassword);
        return allUsers.updatePasswordById(id, newPassword);
    }

    @Override
    public User updatePoints(String username, Long newPoints, String token) {
        var id = allUsers.getUserByUsername(username).getId();
        validateToken(id, token);
        return allUsers.updatePointsById(id, newPoints);
    }
}
