package com.example.demo.web.controllers;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

import com.example.demo.models.dto.GameState;
import com.example.demo.models.dto.User;
import com.example.demo.service.game.GameService;
import com.example.demo.service.gamestate.GameStateService;
import com.example.demo.service.login.LoginService;
import com.example.demo.service.registration.RegistrationService;
import com.example.demo.service.user.UserService;
import com.example.demo.web.exceptions.OpponentNotFoundException;
import com.example.demo.web.exceptions.UserNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
public class UserController {
    private static final Log logger = LogFactory.getLog(UserController.class);
    private final UserService userService;
    private final GameService gameService;
    private final RegistrationService registerService;
    private final LoginService loginService;
    private final GameStateService gameStateService;
    // Key is user who wants to play, value is his future opponent
    private final Map<DeferredResult<User>, User> usersReadyToPlay =
            new ConcurrentHashMap<>();

    UserController(
            UserService userService,
            GameService gameService,
            RegistrationService registerService,
            LoginService loginService,
            GameStateService gameStateService
    ) {
        this.userService = userService;
        this.gameService = gameService;
        this.registerService = registerService;
        this.loginService = loginService;
        this.gameStateService = gameStateService;
    }


    @GetMapping("/users")
    DeferredResult<List<User>> getAllUsers() {
        logger.info("Received get all users request");
        DeferredResult<List<User>> output = new DeferredResult<>(5L, Collections.emptyList());

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            List<User> list = userService.getAll();
            output.setResult(list);
        });

        logger.info("Thread freed");
        return output;
    }

    @GetMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password) {
        return registerService.registerUser(username, password);
    }

    @GetMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password) {
        return loginService.loginUser(username, password);
    }

    @GetMapping("/random")
    DeferredResult<User> randomUser() {
        logger.info("Received random user request");
        DeferredResult<User> output = new DeferredResult<>(5L, new UserNotFoundException());

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            User user = userService.getRandomUser();
            output.setResult(user);
        });

        logger.info("Thread freed");
        return output;
    }


    @GetMapping("/get-user/{id}")
    DeferredResult<User> getUser(@PathVariable Long id) {
        logger.info("Received get user by ID request");
        DeferredResult<User> output = new DeferredResult<>(5L, new UserNotFoundException(id));

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            User user = userService.getUserById(id);
            output.setResult(user);
        });

        logger.info("Thread freed");
        return output;
    }

    private void matchOpponents() {
        if (this.usersReadyToPlay.size() < 2) {
            return;
        }

        var entries = this.usersReadyToPlay.entrySet();

        while (entries.size() > 1) {
            Iterator<Map.Entry<DeferredResult<User>, User>> it = entries.iterator();
            var firstUserEntry = it.next();
            var secondUserEntry = it.next();

            firstUserEntry.getKey().setResult(secondUserEntry.getValue());
            secondUserEntry.getKey().setResult(firstUserEntry.getValue());

            usersReadyToPlay.remove(firstUserEntry.getKey());
            usersReadyToPlay.remove(secondUserEntry.getKey());
        }
    }

    /*
    matches opponents but does not create a game
     */
    @PostMapping("/find-opponent")
    DeferredResult<User> newUser(@RequestBody User newUser) {
        final DeferredResult<User> result = new DeferredResult<>(null, new OpponentNotFoundException());

        this.usersReadyToPlay.put(result, newUser);

        result.onCompletion(() -> usersReadyToPlay.remove(result));

        result.onTimeout(() -> usersReadyToPlay.remove(result));
        matchOpponents();
        return result;
    }

    @PutMapping("/update-username")
    DeferredResult<User> updateUsername(@RequestParam String newUsername, @RequestParam Long id) {
        logger.info("Received update username request");
        DeferredResult<User> output = new DeferredResult<>(5L, new UserNotFoundException(id));

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            var user = userService.updateUsername(id, newUsername);
            output.setResult(user);
        });

        logger.info("Thread freed");
        return output;
    }

    @PutMapping("/update-password")
    DeferredResult<User> updatePassword(@RequestParam String newPassword, @RequestParam Long id) {
        logger.info("Received update password request");
        DeferredResult<User> output = new DeferredResult<>(5L, new UserNotFoundException(id));

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            var user = userService.updatePassword(id, newPassword);
            output.setResult(user);
        });

        logger.info("Thread freed");
        return output;
    }

    @PutMapping("/update-points")
    DeferredResult<User> updatePoints(@RequestParam Long newPoints, @RequestParam Long id) {
        logger.info("Received update points request");
        DeferredResult<User> output = new DeferredResult<>(5L, new UserNotFoundException(id));

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            var user = userService.updatePoints(id, newPoints);
            output.setResult(user);
        });

        logger.info("Thread freed");
        return output;
    }
}