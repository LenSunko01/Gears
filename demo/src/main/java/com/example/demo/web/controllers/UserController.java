package com.example.demo.web.controllers;

import java.util.*;
import java.util.concurrent.ForkJoinPool;

import com.example.demo.models.dto.User;
import com.example.demo.service.login.LoginService;
import com.example.demo.service.registration.RegistrationService;
import com.example.demo.service.user.UserService;
import com.example.demo.web.exceptions.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
public class UserController {
    private static final Log logger = LogFactory.getLog(UserController.class);
    private final UserService userService;
    private final RegistrationService registerService;
    private final LoginService loginService;

    UserController(
            UserService userService,
            RegistrationService registerService,
            LoginService loginService
    ) {
        this.userService = userService;
        this.registerService = registerService;
        this.loginService = loginService;
    }

    public static class UserInformation {
        public String token;
        public Long id;
        public UserInformation(String token, Long id) {
            this.token = token;
            this.id = id;
        }
    }

    @GetMapping("/users")
    DeferredResult<Map<String, Long>> getAllUsers() {
        logger.info("Received get all users request");
        DeferredResult<Map<String, Long>> output = new DeferredResult<>(100L, Collections.emptyList());

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            var list = userService.getAll();
            output.setResult(list);
        });

        logger.info("Thread freed");
        return output;
    }

    @GetMapping("/random")
    DeferredResult<Map.Entry<String, Long>> randomUser() {
        logger.info("Received random user request");
        DeferredResult<Map.Entry<String, Long>> output = new DeferredResult<>(5L, new UserNotFoundException());

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            var user = userService.getRandomUser();
            output.setResult(user);
        });

        logger.info("Thread freed");
        return output;
    }

    private UserInformation getUserInformationByMapEntry(Map.Entry<String, Long> userEntry) {
        return new UserInformation(userEntry.getKey(), userEntry.getValue());
    }

    @PostMapping("/register")
    public UserInformation registerUser(@RequestParam String username, @RequestParam String password) {
        return getUserInformationByMapEntry(registerService.registerUser(username, password));
    }

    @GetMapping("/login")
    public UserInformation loginUser(@RequestParam String username, @RequestParam String password) {
        return getUserInformationByMapEntry(loginService.loginUser(username, password));
    }

    @GetMapping("/get-user")
    DeferredResult<User> getUser(@RequestParam Long id, @RequestParam String token) {
        logger.info("Received get user by ID request");
        DeferredResult<User> output = new DeferredResult<>(100L, new UserNotFoundException(id));

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            try {
                User user = userService.getUserById(id, token);
                output.setResult(user);
            } catch (AuthenticationException e) {
                output.setErrorResult(e);
            }
        });

        logger.info("Thread freed");
        return output;
    }

    public static class GameInfo {
        public Long gameId;
        public boolean isFirstPlayer;
        public GameInfo(Long gameId, boolean isFirstPlayer) {
            this.gameId = gameId;
            this.isFirstPlayer = isFirstPlayer;
        }
    }

    /*
    matches opponents and returns game ID and
    if user is supposed to make the first move true, otherwise false
     */
    @PostMapping("/find-opponent")
    DeferredResult<Map.Entry<Long, Boolean>> newUser(@RequestParam String username, @RequestParam String token) {
        return userService.findOpponent(username, token);
    }

    @PutMapping("/update-username")
    DeferredResult<User> updateUsername(
            @RequestParam String newUsername, @RequestParam Long id, @RequestParam String token
    ) {
        logger.info("Received update username request");
        DeferredResult<User> output = new DeferredResult<>(10L, new UserNotFoundException(id));

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            try {
                var user = userService.updateUsername(id, newUsername, token);
                output.setResult(user);
            } catch (InvalidUsernameException | AuthenticationException e) {
                output.setErrorResult(e);
            }
        });

        logger.info("Thread freed");
        return output;
    }

    @PutMapping("/update-password")
    DeferredResult<User> updatePassword(
            @RequestParam String newPassword, @RequestParam Long id, @RequestParam String token
    ) {
        logger.info("Received update password request");
        DeferredResult<User> output = new DeferredResult<>(10L, new UserNotFoundException(id));

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            try {
                var user = userService.updatePassword(id, newPassword, token);
                output.setResult(user);
            } catch (InvalidPasswordException | AuthenticationException e) {
                output.setErrorResult(e);
            }
        });

        logger.info("Thread freed");
        return output;
    }

    @PutMapping("/update-points")
    DeferredResult<User> updatePoints(
            @RequestParam Long newPoints, @RequestParam Long id, @RequestParam String token
    ) {
        logger.info("Received update points request");
        DeferredResult<User> output = new DeferredResult<>(10L, new UserNotFoundException(id));

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            try {
                var user = userService.updatePoints(id, newPoints, token);
                output.setResult(user);
            } catch (InvalidPasswordException | AuthenticationException e) {
                output.setErrorResult(e);
            }
        });

        logger.info("Thread freed");
        return output;
    }
}