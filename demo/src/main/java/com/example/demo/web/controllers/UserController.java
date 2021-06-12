package com.example.demo.web.controllers;

import com.example.demo.models.dto.User;
import com.example.demo.service.gamestate.GameStateService;
import com.example.demo.service.login.LoginService;
import com.example.demo.service.registration.RegistrationService;
import com.example.demo.service.user.UserService;
import com.example.demo.utils.PictureWrapper;
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
import java.util.List;
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
            try {
                logger.info("Processing GET users in separate thread");
                var list = userService.getAll();
                logger.info("Got map with all users for GET users request");
                output.setResult(ResponseEntity.ok(list));
                logger.info("Set map with all users for GET users request");
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET message request: " + e.getMessage());
            }
            logger.info("Thread freed");
        });

        return output;
    }

    @GetMapping("/rating")
    DeferredResult<ResponseEntity<List<User>>> getUsersByRating() {
        logger.info("Received GET rating request");
        DeferredResult<ResponseEntity<List<User>>> output = new DeferredResult<>(getRatingTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("GET rating request completed"));
        output.onTimeout(() -> {
            logger.info("Timeout during executing GET rating request");
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            try {
                logger.info("Processing GET rating in separate thread");
                var list = userService.getSortedByRatingList(numberOfUsersShownInRating);
                logger.info("Got list with all users for GET rating request");
                output.setResult(ResponseEntity.ok(list));
                logger.info("Set list with all users for GET rating request");
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET message request: " + e.getMessage());
            }
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
            try {
                logger.info("Processing GET random user request in separate thread");
                var user = userService.getRandomUser();
                logger.info("Got random user for the request");
                output.setResult(ResponseEntity.ok(user));
                logger.info("Set random user");
                logger.info("Thread freed");
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET random user request: " + e.getMessage());
            }
        });

        return output;
    }

    @GetMapping("/picture/{id}")
    DeferredResult<ResponseEntity<PictureWrapper>> getPicture(@RequestHeader HttpHeaders headers, @PathVariable Long id) {
        logger.info("Received GET picture user request");
        var token = headers.getFirst("token");
        DeferredResult<ResponseEntity<PictureWrapper>> output = new DeferredResult<>(getPictureTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("GET picture user request completed"));
        output.onTimeout(() -> {
            logger.info("Timeout during executing GET picture user request");
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            try {
                logger.info("Processing GET picture request in separate thread");
                var picture = userService.getPictureById(id, token);
                logger.info("Got picture for the request");
                output.setResult(ResponseEntity.ok(new PictureWrapper(picture)));
                logger.info("Set picture");
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET picture request: " + e.getMessage());
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET picture request: " + e.getMessage());
            }
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
            } catch (InvalidUsernameException | InvalidPasswordException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(e.getMessage()));
                logger.info("Exception while executing register request: " + e.getMessage());
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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
            } catch (InvalidUsernameException | InvalidPasswordException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(e.getMessage()));
                logger.info("Exception while executing register request: " + e.getMessage());
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET picture request: " + e.getMessage());
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET user request: " + e.getMessage());
            }
            logger.info("Thread freed");
        });

        return output;
    }

    private void removeFromMatchingQueue(DeferredResult<Map.Entry<Long, Boolean>> entry) {
        try {
            queueLock.lock();
            usersReadyToPlay.remove(entry);
        } finally {
            queueLock.unlock();
        }
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
            removeFromMatchingQueue(output);
        });
        output.onTimeout(() -> {
            logger.info("Timeout during executing POST find opponent request");
            removeFromMatchingQueue(output);
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
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET picture request: " + e.getMessage());
            } catch (Exception e) {
                logger.info("Exception while executing POST find opponent request: " + e.getMessage());
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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

    @PutMapping("/username/{id}")
    DeferredResult<User> updateUsername(
            @RequestHeader HttpHeaders headers, @RequestBody String newUsername, @PathVariable Long id
    ) {
        logger.info("Received PUT username request");
        var token = headers.getFirst("token");
        DeferredResult<User> output = new DeferredResult<>(putUsernameTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("PUT request completed"));
        output.onTimeout(() -> {
            logger.info("Timeout during executing PUT username request");
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            User user;
            try {
                user = userService.updateUsername(id, newUsername, token);
                output.setResult(user);
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing PUT username request: " + e.getMessage());
            } catch (Exception e) {
                logger.info("Exception while executing PUT username request: " + e.getMessage());
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
            }
            logger.info("Thread freed");
        });

        return output;
    }

    @PutMapping("/picture/{id}")
    DeferredResult<User> updatePicture(
            @RequestHeader HttpHeaders headers, @RequestBody byte[] newPicture, @PathVariable Long id
    ) {
        logger.info("Received PUT picture request");
        var token = headers.getFirst("token");
        DeferredResult<User> output = new DeferredResult<>(putPictureTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("PUT request completed"));
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
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing PUT username request: " + e.getMessage());
            } catch (Exception e) {
                logger.info("Exception while executing PUT picture request: " + e.getMessage());
                output.setErrorResult(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(e.getMessage()));
            }
            logger.info("Thread freed");
        });

        return output;
    }

    @PutMapping("/password/{id}")
    DeferredResult<User> updatePassword(
            @RequestHeader HttpHeaders headers, @RequestBody String newPassword, @PathVariable Long id
    ) {
        logger.info("Received PUT password request");
        var token = headers.getFirst("token");
        DeferredResult<User> output = new DeferredResult<>(putPasswordTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("PUT request completed"));
        output.onTimeout(() -> {
            logger.info("Timeout during executing PUT password request");
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            User user;
            try {
                user = userService.updatePassword(id, newPassword, token);
                output.setResult(user);
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing PUT password request: " + e.getMessage());
            } catch (InvalidPasswordException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(e.getMessage()));
                logger.info("Exception while executing PUT password request: " + e.getMessage());
            } catch (Exception e) {
                logger.info("Exception while executing PUT password request: " + e.getMessage());
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
            }
            logger.info("Thread freed");
        });

        return output;
    }

    @PutMapping("/points/{id}")
    DeferredResult<User> updatePoints(
            @RequestHeader HttpHeaders headers, @RequestBody Long newPoints, @PathVariable Long id
    ) {
        logger.info("Received PUT points request");
        var token = headers.getFirst("token");
        DeferredResult<User> output = new DeferredResult<>(putPointsTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("PUT request completed"));
        output.onTimeout(() -> {
            logger.info("Timeout during executing PUT points request");
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            User user;
            try {
                user = userService.updatePoints(id, newPoints, token);
                output.setResult(user);
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing PUT points request: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(e.getMessage()));
                logger.info("Exception while executing PUT points request: " + e.getMessage());
            } catch (Exception e) {
                logger.info("Exception while executing PUT points request: " + e.getMessage());
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
            }
            logger.info("Thread freed");
        });

        return output;
    }
}