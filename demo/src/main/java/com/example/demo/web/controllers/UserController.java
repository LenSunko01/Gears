package com.example.demo.web.controllers;

import com.example.demo.models.dto.User;
import com.example.demo.service.gamestate.GameStateService;
import com.example.demo.service.login.LoginService;
import com.example.demo.service.registration.RegistrationService;
import com.example.demo.service.user.UserService;
import com.example.demo.web.exceptions.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.example.demo.web.controllers.ControllersConstants.*;

@RestController
public class UserController {
    private static final Log logger = LogFactory.getLog(UserController.class);
    private final UserService userService;
    private final RegistrationService registerService;
    private final LoginService loginService;
    private final GameStateService gameStateService;
    private final Lock queueLock = new ReentrantLock();
    // Key is user who wants to play, value is his future opponent
    private final ConcurrentHashMap<DeferredResult<Map.Entry<Long, Boolean>>, User> usersReadyToPlay =
            new ConcurrentHashMap<>();

    UserController(
            UserService userService,
            RegistrationService registerService,
            LoginService loginService,
            GameStateService gameStateService) {
        this.userService = userService;
        this.registerService = registerService;
        this.loginService = loginService;
        this.gameStateService = gameStateService;
    }

    @GetMapping("/users")
    DeferredResult<ResponseEntity<Map<String, Long>>> getAllUsers() {
        logger.info("Received GET users request");
        DeferredResult<ResponseEntity<Map<String, Long>>> output = new DeferredResult<>(getUsersTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("GET users request completed"));
        output.onTimeout(() -> {
            logger.info("Timeout during executing GET users request");
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing GET users in separate thread");
            var list = userService.getAll();
            logger.info("Got map with all users for GET users request");
            output.setResult(ResponseEntity.ok(list));
            logger.info("Set map with all users for GET users request");
            logger.info("Thread freed");
        });

        return output;
    }

    @GetMapping("/random")
    DeferredResult<ResponseEntity<Map.Entry<String, Long>>> randomUser() {
        logger.info("Received GET random user request");
        DeferredResult<ResponseEntity<Map.Entry<String, Long>>> output = new DeferredResult<>(getRandomUserTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("GET random user request completed"));
        output.onTimeout(() -> {
            logger.info("Timeout during executing GET random user request");
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing GET random user request in separate thread");
            var user = userService.getRandomUser();
            logger.info("Got random user for the request");
            output.setResult(ResponseEntity.ok(user));
            logger.info("Set random user");
            logger.info("Thread freed");
        });

        return output;
    }

    @PostMapping("/register")
    public DeferredResult<ResponseEntity<User.UserInformation>> registerUser(@RequestParam String username, @RequestParam String password) {
        logger.info("Received POST register user request");
        DeferredResult<ResponseEntity<User.UserInformation>> output = new DeferredResult<>(postRegisterUserTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("POST register user request completed"));
        output.onTimeout(() -> {
            logger.info("Timeout during executing POST register user request");
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing POST register user request in separate thread");
            try {
                var userEntry = registerService.registerUser(username, password);
                logger.info("Registered user");
                output.setResult(ResponseEntity.ok(userEntry));
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(e.getMessage()));
                logger.info("Exception while executing register request: " + e.getMessage());
            }
            logger.info("Thread freed");
        });

        return output;
    }

    @PostMapping("/login")
    public DeferredResult<ResponseEntity<User.UserInformation>> loginUser(@RequestParam String username, @RequestParam String password) {
        logger.info("Received POST login user request");
        DeferredResult<ResponseEntity<User.UserInformation>> output = new DeferredResult<>(postLoginUserTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("POST login user request completed"));
        output.onTimeout(() -> {
            logger.info("Timeout during executing POST login user request");
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing POST login user request in separate thread");
            try {
                var userEntry = loginService.loginUser(username, password);
                logger.info("User logged in");
                output.setResult(ResponseEntity.ok(userEntry));
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(e.getMessage()));
                logger.info("Exception while executing login request: " + e.getMessage());
            }
            logger.info("Thread freed");
        });

        return output;
    }

    @GetMapping("/user/{id}")
    DeferredResult<ResponseEntity<User>> getUser(@RequestHeader HttpHeaders headers, @PathVariable Long id) {
        logger.info("Received GET user by ID request");
        var token = headers.getFirst("token");
        DeferredResult<ResponseEntity<User>> output = new DeferredResult<>(getUserTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("GET user request completed"));
        output.onTimeout(() -> {
            logger.info("Timeout during executing GET user request");
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            try {
                User user = userService.getUserById(id, token);
                logger.info("Got user for GET user request");
                output.setResult(ResponseEntity.ok(user));
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET user request: " + e.getMessage());
            }
            logger.info("Thread freed");
        });

        return output;
    }

    /*
    matches opponents and returns game ID and
    if user is supposed to make the first move true, otherwise false
     */
    @PostMapping("/find/opponent")
    DeferredResult<Map.Entry<Long, Boolean>> findOpponent(@RequestHeader HttpHeaders headers, @RequestParam String username) {
        logger.info("Received POST find opponent request");
        var token = headers.getFirst("token");
        DeferredResult<Map.Entry<Long, Boolean>> output = new DeferredResult<>(postFindOpponentTimeoutInMilliseconds);
        output.onCompletion(() -> {
            logger.info("POST find opponent request completed for " + username);
            try {
                queueLock.lock();
                usersReadyToPlay.remove(output);
            } finally {
                 queueLock.unlock();
            }
        });
        output.onTimeout(() -> {
            logger.info("Timeout during executing POST find opponent request");
            try {
                queueLock.lock();
                usersReadyToPlay.remove(output);
            } finally {
                queueLock.unlock();
            }
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            User user;
            try {
                user = userService.getUserByUsername(username, token);
                logger.info("Putting opponents to matching queue");
                try {
                    queueLock.lock();
                    usersReadyToPlay.put(output, user);
                    logger.info(usersReadyToPlay.size());
                } finally {
                    queueLock.unlock();
                }
                logger.info("Trying to match opponents");
                matchOpponents();
            } catch (Exception e) {
                logger.info("Exception while executing POST find opponent request: " + e.getMessage());
                output.setErrorResult(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(e.getMessage()));
            }
            logger.info("Thread freed");
        });

        return output;
    }

    private void matchOpponents() {
        try {
            queueLock.lock();
            if (this.usersReadyToPlay.size() < 2) {
                return;
            }

            var entries = this.usersReadyToPlay.entrySet();

            while (entries.size() > 1) {
                logger.info("---------------Queue size is " + entries.size());
                for (var entry : entries) {
                    logger.info(entry.getValue().getUsername() + " ");
                }
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

                logger.info("---------------matched " +
                        firstUserEntry.getValue().getUsername() + " and " +
                        secondUserEntry.getValue().getUsername());
                usersReadyToPlay.remove(firstUserEntry.getKey());
                usersReadyToPlay.remove(secondUserEntry.getKey());
                logger.info("---------------Queue size is " + entries.size());
            }
        } finally {
            queueLock.unlock();
        }
    }

    // user/{id}/name
    // id -pathvar newUsername - requestParam
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

    @PutMapping("/picture")
    DeferredResult<User> updatePicture(
            @RequestHeader HttpHeaders headers, @RequestBody byte[] newPicture, @PathVariable Long id
    ) {
        logger.info("Received PUT picture request");
        var token = headers.getFirst("token");
        DeferredResult<User> output = new DeferredResult<User>(60000L);
        output.onCompletion(() -> {
            logger.info("PUT request completed");
        });
        output.onTimeout(() -> {
            logger.info("Timeout during executing PUT picture request");
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            User user;
            try {
                user = userService.updatePicture(id, newPicture, token);
                output.setResult(user);
            } catch (Exception e) {
                logger.info("Exception while executing PUT picture request: " + e.getMessage());
                output.setErrorResult(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(e.getMessage()));
            }
            logger.info("Thread freed");
        });

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