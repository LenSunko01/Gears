package com.example.demo.web.controllers;

import com.example.demo.models.dto.User;
import com.example.demo.service.login.LoginService;
import com.example.demo.service.registration.RegistrationService;
import com.example.demo.service.user.UserService;
import com.example.demo.web.exceptions.AuthenticationException;
import com.example.demo.web.exceptions.InvalidPasswordException;
import com.example.demo.web.exceptions.InvalidUsernameException;
import com.example.demo.web.exceptions.UserNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

import static com.example.demo.web.controllers.ControllersConstants.*;

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
    // /find/opponent
    @PostMapping("/find-opponent")
    DeferredResult<Map.Entry<Long, Boolean>> newUser(@RequestParam String username, @RequestParam String token) {
        return userService.findOpponent(username, token);
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