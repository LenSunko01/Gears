package com.example.demo.web.controllers;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

import com.example.demo.models.dto.User;
import com.example.demo.service.user.UserService;
import com.example.demo.web.exceptions.OpponentNotFoundException;
import com.example.demo.web.exceptions.UserNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
public class UserController {
    private static final Log logger = LogFactory.getLog(UserController.class);
    private final UserService userService;
    // Key is user who wants to play, value is his future opponent
    private final Map<DeferredResult<User>, User> usersReadyToPlay =
            new ConcurrentHashMap<>();

    UserController(UserService userService) {
        this.userService = userService;
    }


    // get all users who are currently playing (not waiting to play)
    @GetMapping("/users")
    DeferredResult<List<User>> all() {
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


    @GetMapping("/users/{id}")
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

        for (Iterator<Map.Entry<DeferredResult<User>, User>> it = entries.iterator(); it.hasNext(); ) {
            var firstUserEntry = it.next();
            Map.Entry<DeferredResult<User>, User> secondUserEntry;

            if (!it.hasNext()) {
                break;
            } else {
                secondUserEntry = it.next();
            }

            firstUserEntry.getKey().setResult(secondUserEntry.getValue());
            secondUserEntry.getKey().setResult(firstUserEntry.getValue());

            // TODO: create new game with given users
            userService.addUser(firstUserEntry.getValue());
            userService.addUser(secondUserEntry.getValue());
        }
    }

    @PostMapping("/user")
    DeferredResult<User> newUser(@RequestBody User newUser) {
        final DeferredResult<User> result = new DeferredResult<>(5L, new OpponentNotFoundException());

        this.usersReadyToPlay.put(result, newUser);

        result.onCompletion(() -> usersReadyToPlay.remove(result));

        result.onTimeout(() -> usersReadyToPlay.remove(result));

        matchOpponents();
        return result;
    }

    @PutMapping("/users/{id}")
    DeferredResult<User> updateUser(@RequestBody User newUser, @PathVariable Long id) {
        logger.info("Received update user request");
        DeferredResult<User> output = new DeferredResult<>(5L, new UserNotFoundException(id));

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            User user = userService.updateUser(id, newUser);
            output.setResult(user);
        });

        logger.info("Thread freed");
        return output;
    }

    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Long id) {
        logger.info("Received delete user request");
        userService.deleteUserById(id);
    }
}