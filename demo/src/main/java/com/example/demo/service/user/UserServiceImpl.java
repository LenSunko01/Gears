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
    // Key is user who wants to play, value is his future opponent
    private final Map<DeferredResult<Map.Entry<Long, Boolean>>, User> usersReadyToPlay =
            new ConcurrentHashMap<>();

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

    private void matchOpponents() {
        if (this.usersReadyToPlay.size() < 2) {
            return;
        }

        var entries = this.usersReadyToPlay.entrySet();

        while (entries.size() > 1) {
            Iterator<Map.Entry<DeferredResult<Map.Entry<Long, Boolean>>, User>> it = entries.iterator();
            var firstUserEntry = it.next();
            var firstUser = firstUserEntry.getValue();
            var secondUserEntry = it.next();
            var secondUser = secondUserEntry.getValue();

            var gameId = gameStateService.setGame(firstUser, secondUser);
            var firstPlayerGameInfo = new AbstractMap.SimpleEntry<>(gameId, true);
            var secondPlayerGameInfo = new AbstractMap.SimpleEntry<>(gameId, false);
            firstUserEntry.getKey().setResult(firstPlayerGameInfo);
            secondUserEntry.getKey().setResult(secondPlayerGameInfo);

            usersReadyToPlay.remove(firstUserEntry.getKey());
            usersReadyToPlay.remove(secondUserEntry.getKey());
        }
    }

    public DeferredResult<Map.Entry<Long, Boolean>> findOpponent(String username, String token) {
        final DeferredResult<Map.Entry<Long, Boolean>> result = new DeferredResult<>(10000L, new OpponentNotFoundException());
        logger.info("Trying to get user" + username + "by username from database");
        var user = allUsers.getUserByUsername(username);
        var id = user.getId();
        logger.info("Got user from database");
        try {
            logger.info("Trying to validate token");
            validateToken(id, token);
            logger.info("Validated token");
        } catch (AuthenticationException e) {
            logger.info("Got authentication exception");
            result.setErrorResult(e);
            return result;
        }
        this.usersReadyToPlay.put(result, user);
        result.onCompletion(() -> usersReadyToPlay.remove(result));
        result.onTimeout(() -> usersReadyToPlay.remove(result));
        matchOpponents();
        return result;
    }
}
