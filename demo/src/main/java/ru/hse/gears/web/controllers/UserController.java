package ru.hse.gears.web.controllers;

import ru.hse.gears.models.dto.User;
import ru.hse.gears.service.gamestate.GameStateService;
import ru.hse.gears.service.login.LoginService;
import ru.hse.gears.service.registration.RegistrationService;
import ru.hse.gears.service.user.UserService;
import ru.hse.gears.utils.PictureWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import ru.hse.gears.web.exceptions.AuthenticationException;
import ru.hse.gears.web.exceptions.InvalidPasswordException;
import ru.hse.gears.web.exceptions.InvalidUsernameException;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
        logger.info("--------------> Received GET users request");
        DeferredResult<ResponseEntity<Map<String, Long>>> output = new DeferredResult<>(ControllersConstants.getUsersTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("GET users request completed"));
        output.onTimeout(() -> {
            logger.info("TIMEOUT: Timeout during executing GET users request");
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            try {
                logger.info("Processing GET users request in separate thread");
                var list = userService.getAll();
                output.setResult(ResponseEntity.ok(list));
                logger.info("Completed GET users request");
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET users request: " + e.getMessage());
            }
            logger.info("Thread freed from GET users request");
        });

        return output;
    }

    @GetMapping("/rating")
    DeferredResult<ResponseEntity<List<User>>> getUsersByRating() {
        logger.info("--------------> Received GET rating request");
        DeferredResult<ResponseEntity<List<User>>> output = new DeferredResult<>(ControllersConstants.getRatingTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("GET rating request completed"));
        output.onTimeout(() -> {
            logger.info("TIMEOUT: Timeout during executing GET rating request");
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            try {
                logger.info("Processing GET rating in separate thread");
                var list = userService.getSortedByRatingList(ControllersConstants.numberOfUsersShownInRating);
                output.setResult(ResponseEntity.ok(list));
                logger.info("Completed GET rating request");
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET rating request: " + e.getMessage());
            }
            logger.info("Thread freed from GET rating request");
        });

        return output;
    }

    @GetMapping("/random")
    DeferredResult<ResponseEntity<Map.Entry<String, Long>>> randomUser() {
        logger.info("--------------> Received GET random request");
        DeferredResult<ResponseEntity<Map.Entry<String, Long>>> output = new DeferredResult<>(ControllersConstants.getRandomUserTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("GET random request completed"));
        output.onTimeout(() -> {
            logger.info("TIMEOUT: Timeout during executing GET random request");
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            try {
                logger.info("Processing GET random in separate thread");
                var user = userService.getRandomUser();
                output.setResult(ResponseEntity.ok(user));
                logger.info("Completed GET random request");
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET random user request: " + e.getMessage());
            }
            logger.info("Thread freed from GET random request");
        });

        return output;
    }

    @GetMapping("/picture/{id}")
    DeferredResult<ResponseEntity<PictureWrapper>> getPicture(@RequestHeader HttpHeaders headers, @PathVariable Long id) {
        logger.info("--------------> Received GET picture request with id " + id);
        var token = headers.getFirst("token");
        DeferredResult<ResponseEntity<PictureWrapper>> output = new DeferredResult<>(ControllersConstants.getPictureTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("GET picture request completed with id " + id));
        output.onTimeout(() -> {
            logger.info("TIMEOUT: Timeout during executing GET picture request with id " + id);
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            try {
                logger.info("Processing GET picture request with id " + id + " in separate thread");
                var picture = userService.getPictureById(id, token);
                output.setResult(ResponseEntity.ok(new PictureWrapper(picture)));
                logger.info("Completed GET picture request with id " + id);
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET picture request with id " + id + " : " + e.getMessage());
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET picture request with id " + id + " : " + e.getMessage());
            }
            logger.info("Thread freed from GET picture request with id " + id);
        });

        return output;
    }

    @PostMapping("/register")
    public DeferredResult<ResponseEntity<User.UserInformation>> registerUser(@RequestParam String username, @RequestParam String password) {
        logger.info("--------------> Received POST register request with username " + username);
        DeferredResult<ResponseEntity<User.UserInformation>> output = new DeferredResult<>(ControllersConstants.postRegisterUserTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("POST register request completed with username " + username));
        output.onTimeout(() -> {
            logger.info("TIMEOUT: Timeout during executing POST register request with username " + username);
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing POST register request with username " + username + " in separate thread");
            try {
                var userEntry = registerService.registerUser(username, password);
                logger.info("Registered user " + username);
                output.setResult(ResponseEntity.ok(userEntry));
            } catch (InvalidUsernameException | InvalidPasswordException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(e.getMessage()));
                logger.info("Exception while executing register request with username " + username + " : " + e.getMessage());
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing register request with username " + username + " : " + e.getMessage());
            }

            logger.info("Thread freed from POST register request with username " + username);
        });

        return output;
    }

    @PostMapping("/login")
    public DeferredResult<ResponseEntity<User.UserInformation>> loginUser(@RequestParam String username, @RequestParam String password) {
        logger.info("--------------> Received POST login request with username " + username);
        DeferredResult<ResponseEntity<User.UserInformation>> output = new DeferredResult<>(ControllersConstants.postLoginUserTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("POST login user request completed with username " + username));
        output.onTimeout(() -> {
            logger.info("TIMEOUT: Timeout during executing POST login user request with username " + username);
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing POST login user request with username " + username + " in separate thread");
            try {
                var userEntry = loginService.loginUser(username, password);
                logger.info("User logged in with username " + username);
                output.setResult(ResponseEntity.ok(userEntry));
            } catch (InvalidUsernameException | InvalidPasswordException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(e.getMessage()));
                logger.info("Exception while executing login request with username " + username + " : " + e.getMessage());
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing login request with username " + username + " : " + e.getMessage());
            }

            logger.info("Thread freed from POST login request with username " + username);
        });

        return output;
    }

    @GetMapping("/user/{id}")
    DeferredResult<ResponseEntity<User>> getUser(@RequestHeader HttpHeaders headers, @PathVariable Long id) {
        logger.info("--------------> Received GET user request with id " + id);
        var token = headers.getFirst("token");
        DeferredResult<ResponseEntity<User>> output = new DeferredResult<>(ControllersConstants.getUserTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("GET user request completed with id " + id));
        output.onTimeout(() -> {
            logger.info("TIMEOUT: Timeout during executing GET user request with id " + id);
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing GET user request with id " + id + " in separate thread");
            try {
                User user = userService.getUserById(id, token);
                logger.info("Completed GET user request with id " + id);
                output.setResult(ResponseEntity.ok(user));
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET user request with id " + id + " : " + e.getMessage());
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET user request with id " + id + " : " + e.getMessage());
            }

            logger.info("Thread freed from GET user request with id " + id);
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
        logger.info("--------------> Received GET user request with username " + username);
        var token = headers.getFirst("token");
        DeferredResult<Map.Entry<Long, Boolean>> output = new DeferredResult<>(ControllersConstants.postFindOpponentTimeoutInMilliseconds);
        output.onCompletion(() -> {
            logger.info("POST find opponent request completed for " + username);
            removeFromMatchingQueue(output);
        });
        output.onTimeout(() -> {
            logger.info("TIMEOUT: Timeout during executing POST find opponent request for " + username);
            removeFromMatchingQueue(output);
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing POST find opponent request with username " + username + " in separate thread");
            User user;
            try {
                user = userService.getUserByUsername(username, token);
                logger.info("Putting " + username + " to matching queue");
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
                logger.info("Exception while executing POST find opponent request with username " + username + " : " + e.getMessage());
            } catch (Exception e) {

                logger.info("Exception while executing POST find opponent request with username " + username + " : " + e.getMessage());
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
            }

            logger.info("Thread freed from POST find opponent request with username " + username);
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
                logger.info("Trying to match opponents, queue size is " + entries.size());
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

                logger.info("Matched " +
                        firstUserEntry.getValue().getUsername() + " and " +
                        secondUserEntry.getValue().getUsername());
                usersReadyToPlay.remove(firstUserEntry.getKey());
                usersReadyToPlay.remove(secondUserEntry.getKey());
                logger.info("Matched, queue size is " + entries.size());
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
        DeferredResult<User> output = new DeferredResult<>(ControllersConstants.putUsernameTimeoutInMilliseconds);
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
        DeferredResult<User> output = new DeferredResult<>(ControllersConstants.putPictureTimeoutInMilliseconds);
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
        DeferredResult<User> output = new DeferredResult<>(ControllersConstants.putPasswordTimeoutInMilliseconds);
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
        DeferredResult<User> output = new DeferredResult<>(ControllersConstants.putPointsTimeoutInMilliseconds);
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