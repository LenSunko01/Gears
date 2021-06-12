package com.example.demo.web.controllers;

import com.example.demo.models.dto.Board;
import com.example.demo.models.dto.GameState;
import com.example.demo.models.dto.Message;
import com.example.demo.service.gamestate.GameStateService;
import com.example.demo.web.exceptions.AuthenticationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.AbstractMap;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.example.demo.web.controllers.ControllersConstants.*;

@RestController
public class GameStateController {
    private static final Log logger = LogFactory.getLog(GameStateController.class);
    private final GameStateService gameStateService;
    ConcurrentHashMap<Long, Lock> lockGameMap = new ConcurrentHashMap<>();
    ConcurrentHashMap<Long, Lock> lockMessageMap = new ConcurrentHashMap<>();
    ConcurrentHashMap<Long, Condition> conditionGameChangedMap = new ConcurrentHashMap<>();
    ConcurrentHashMap<Long, Condition> conditionInitMap = new ConcurrentHashMap<>();
    ConcurrentHashMap<Long, Condition> conditionMessageMap = new ConcurrentHashMap<>();
    ConcurrentHashMap<AbstractMap.SimpleEntry<Long, GameState.CurrentPlayer>, ConcurrentLinkedQueue<Message>> messages = new ConcurrentHashMap<>();

    public GameStateController(GameStateService gameStateService) {
        this.gameStateService = gameStateService;
    }

    /*
    {
    "id": 2472244993064557546,
    "scoreOfFirstPlayer": 0,
    "scoreOfSecondPlayer": 0,
    "firstPlayerBoard": {
        "gears": null,
        "rightGutter": {
            "degree": 60,
            "howManyBalls": 1,
            "howManyBallsStart": 1
        },
        "leftGutter": {
            "degree": 300,
            "howManyBalls": 1,
            "howManyBallsStart": 1
        },
        "pot": {
            "degree": 120,
            "howManyBalls": 0
        },
        "step": 10,
        "allBallsInPot": false
    },
    "secondPlayerBoard": {
        "gears": null,
        "rightGutter": {
            "degree": 60,
            "howManyBalls": 1,
            "howManyBallsStart": 1
        },
        "leftGutter": {
            "degree": 300,
            "howManyBalls": 1,
            "howManyBallsStart": 1
        },
        "pot": {
            "degree": 120,
            "howManyBalls": 0
        },
        "step": 10,
        "allBallsInPot": false
    },
    "users": [
        {
            "id": 11,
            "username": "Ilya",
            "password": "zhopa",
            "points": 700,
            "totalNumberOfGames": 2,
            "numberOfGamesWon": 1,
            "numberOfGamesLost": 1
        },
        {
            "id": 15,
            "username": "NG",
            "password": "zhopa",
            "points": 200,
            "totalNumberOfGames": 2,
            "numberOfGamesWon": 1,
            "numberOfGamesLost": 1
        }
    ],
    "turn": {
        "numberOfActiveGear": -1,
        "degree": null
    },
    "currentGameState": "CONTINUE",
    "firstPlayerHasInitializedBoard": false,
    "secondPlayerHasInitializedBoard": false,
    "firstPlayerHasEndedGame": false,
    "secondPlayerHasEndedGame": false,
    "currentPlayer": "FIRSTPLAYER"
}
     */
    @GetMapping("/game/{id}/player/{currentPlayer}")
    DeferredResult<ResponseEntity<GameState>> gameState(
            @RequestHeader HttpHeaders headers,
            @PathVariable Long id,
            @PathVariable GameState.CurrentPlayer currentPlayer
    ) {
        logger.info("Received GET game state by ID request");
        var token = headers.getFirst("token");
        DeferredResult<ResponseEntity<GameState>> output = new DeferredResult<>(getGameTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("GET game by ID request completed"));
        output.onTimeout(() -> {
            logger.info("Timeout during executing GET game request");
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            Lock lock = new ReentrantLock();
            try {
                lock = lockGameMap.get(id);
                lock.lock();
                var res = gameStateService.getStateById(id, token, currentPlayer);
                while (!(res.getCurrentPlayer().equals(currentPlayer)
                        && res.isFirstPlayerHasInitializedBoard()
                        && res.isSecondPlayerHasInitializedBoard())
                ) {
                    logger.info("Get game waiting for condition " + currentPlayer);
                    var condition = conditionGameChangedMap.get(id);
                    condition.await();
                    res = gameStateService.getStateById(id, token, currentPlayer);
                }
                logger.info("Set game");
                output.setResult(ResponseEntity.ok(res));
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET game request: " + e.getMessage());
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET game request: " + e.getMessage());
            } finally {
                lock.unlock();
            }
            logger.info("Thread freed");
        });

        return output;
    }

    @PostMapping("/game/{id}/player/{currentPlayer}")
    DeferredResult<ResponseEntity<String>> updateGameState(
            @RequestHeader HttpHeaders headers,
            @PathVariable Long id,
            @PathVariable GameState.CurrentPlayer currentPlayer,
            @RequestBody GameState newGameState
    ) {
        logger.info("Received POST game state by ID request " + currentPlayer);
        var token = headers.getFirst("token");
        DeferredResult<ResponseEntity<String>> output = new DeferredResult<>(postGameTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("POST game by ID request completed"));
        output.onTimeout(() -> {
            logger.info("Timeout during executing POST game request");
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            Lock lock = new ReentrantLock();
            try {
                lock = lockGameMap.get(id);
                lock.lock();
                gameStateService.updateStateById(id, token, newGameState, currentPlayer);
                output.setResult(ResponseEntity.ok("Game updated"));
                conditionGameChangedMap.get(id).signal();
                logger.info("Game updated");
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing POST game request: " + e.getMessage());
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing POST game request: " + e.getMessage());
            } finally {
                lock.unlock();
            }
            logger.info("Thread freed");
        });

        return output;
    }

    @GetMapping("/board/{id}")
    DeferredResult<ResponseEntity<GameState>> getBoardFromFirstPlayer(
            @RequestHeader HttpHeaders headers,
            @PathVariable Long id
    ) {
        var token = headers.getFirst("token");
        DeferredResult<ResponseEntity<GameState>> output = new DeferredResult<>(getGameTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("GET board by ID request completed"));
        output.onTimeout(() -> {
            logger.info("Timeout during executing GET board request");
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            onGameCreation(id);
            Lock lock = new ReentrantLock();
            try {
                lock = lockGameMap.get(id);
                lock.lock();
                var res = gameStateService.getStateById(id, token, GameState.CurrentPlayer.SECONDPLAYER);
                while (!res.isFirstPlayerHasInitializedBoard()) {
                    var condition = conditionInitMap.get(id);
                    condition.await();
                    res = gameStateService.getStateById(id, token, GameState.CurrentPlayer.SECONDPLAYER);
                }
                output.setResult(ResponseEntity.ok(res));
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET board request: " + e.getMessage());
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET board request: " + e.getMessage());
            } finally {
                lock.unlock();
            }
            logger.info("Thread freed");
        });
        return output;
    }

    private void onGameCreation(Long id) {
        lockGameMap.putIfAbsent(id, new ReentrantLock());
        lockMessageMap.putIfAbsent(id, new ReentrantLock());
        var lockGame = lockGameMap.get(id);
        conditionInitMap.putIfAbsent(id, lockGame.newCondition());
        conditionGameChangedMap.putIfAbsent(id, lockGame.newCondition());
        var lockMessage = lockMessageMap.get(id);
        conditionMessageMap.putIfAbsent(id, lockMessage.newCondition());
        var playerEntry = new AbstractMap.SimpleEntry<>(id, GameState.CurrentPlayer.FIRSTPLAYER);
        messages.putIfAbsent(playerEntry, new ConcurrentLinkedQueue<>());
        playerEntry = new AbstractMap.SimpleEntry<>(id, GameState.CurrentPlayer.SECONDPLAYER);
        messages.putIfAbsent(playerEntry, new ConcurrentLinkedQueue<>());
        logger.info("On game creation completed");
    }

    @PostMapping("/init/{id}/player/{currentPlayer}")
    DeferredResult<ResponseEntity<GameState>> initBoard(
            @RequestHeader HttpHeaders headers,
            @PathVariable Long id,
            @PathVariable GameState.CurrentPlayer currentPlayer,
            @RequestBody Board board
    ) {
        logger.info("Received POST init game state by ID request " + currentPlayer);
        var token = headers.getFirst("token");
        logger.info("****************" + currentPlayer.toString() + " " + token);
        DeferredResult<ResponseEntity<GameState>> output = new DeferredResult<>(initGameTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("POST init game by ID request completed"));
        output.onTimeout(() -> {
            logger.info("Timeout during executing POST init game request");
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            onGameCreation(id);
            Lock lock = new ReentrantLock();
            try {
                lock = lockGameMap.get(id);
                lock.lock();
                var res = gameStateService.updateBoardById(id, token, currentPlayer, board);
                logger.info(res.getFirstPlayerBoard().equals(board));
                output.setResult(ResponseEntity.ok(res));
                conditionInitMap.get(id).signal();
                logger.info("Completed POST init game request");
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing POST init game request1: " + e.getMessage());
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing POST init game request2: " + e.getMessage());
            } finally {
                lock.unlock();
            }
            logger.info("Thread freed");
        });

        return output;
    }

    private void onGameDeletion(Long id) {
        var lock = lockGameMap.get(id);
        if (lock == null) {
            return;
        }
        try {
            lock.lock();
            if (!gameStateService.checkGameExists(id)) {
                lockGameMap.remove(id);
                lockMessageMap.remove(id);
                conditionInitMap.remove(id);
                conditionGameChangedMap.remove(id);
                conditionMessageMap.remove(id);
                messages.remove(new AbstractMap.SimpleEntry<>(id, GameState.CurrentPlayer.FIRSTPLAYER));
                messages.remove(new AbstractMap.SimpleEntry<>(id, GameState.CurrentPlayer.SECONDPLAYER));
            }
        } finally {
            lock.unlock();
        }
    }

    @DeleteMapping("/game/{id}/player/{currentPlayer}")
    DeferredResult<ResponseEntity<String>> deleteGame(
            @RequestHeader HttpHeaders headers,
            @PathVariable Long id,
            @PathVariable GameState.CurrentPlayer currentPlayer
    ) {
        logger.info("Received DELETE game state by ID request");
        var token = headers.getFirst("token");
        DeferredResult<ResponseEntity<String>> output = new DeferredResult<>(deleteGameTimeoutInMilliseconds);
        output.onCompletion(() -> {
            onGameDeletion(id);
            logger.info("DELETE game by ID request completed");
        });
        output.onTimeout(() -> {
            onGameDeletion(id);
            logger.info("Timeout during executing DELETE game request");
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            try {
                gameStateService.deleteGameState(id, token, currentPlayer);
                output.setResult(ResponseEntity.ok("Game deleted"));
                logger.info("Completed DELETE game request");
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing DELETE game request: " + e.getMessage());
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing DELETE game request: " + e.getMessage());
            }
            logger.info("Thread freed");
        });

        return output;
    }

    @PostMapping("/message/{id}/player/{currentPlayer}")
    DeferredResult<ResponseEntity<String>> sendMessage(
            @RequestHeader HttpHeaders headers,
            @RequestBody Message message,
            @PathVariable Long id,
            @PathVariable GameState.CurrentPlayer currentPlayer
    ) {
        logger.info("Received POST message by ID request");
        var token = headers.getFirst("token");
        DeferredResult<ResponseEntity<String>> output = new DeferredResult<>(postMessageTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("POST message by ID request completed"));
        output.onTimeout(() -> {
            logger.info("Timeout during executing POST message request");
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            Lock lock = new ReentrantLock();
            try {
                lock = lockMessageMap.get(id);
                lock.lock();
                if (!gameStateService.checkGameExists(id)) {
                    throw new Exception("Game does not exist");
                }
                if (!gameStateService.validateToken(id, token, currentPlayer)) {
                    throw new AuthenticationException();
                }
                var playerEntry = new AbstractMap.SimpleEntry<>(id, currentPlayer);
                messages.get(playerEntry).add(message);
                var condition = conditionMessageMap.get(id);
                condition.signalAll();
                output.setResult(ResponseEntity.ok("Message sent"));
                logger.info("Completed POST message request");
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing POST message request: " + e.getMessage());
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing POST message request: " + e.getMessage());
            } finally {
                lock.unlock();
            }
            logger.info("Thread freed");
        });

        return output;
    }

    @GetMapping("/message/{id}/player/{currentPlayer}")
    DeferredResult<ResponseEntity<Message>> getMessage(
            @RequestHeader HttpHeaders headers,
            @PathVariable Long id,
            @PathVariable GameState.CurrentPlayer currentPlayer
    ) {
        logger.info("Received GET message by ID request");
        var token = headers.getFirst("token");
        DeferredResult<ResponseEntity<Message>> output = new DeferredResult<>(getMessageTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("GET message by ID request completed"));
        output.onTimeout(() -> {
            logger.info("Timeout during executing GET message request");
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing in separate thread");
            Lock lock = new ReentrantLock();
            try {
                lock = lockMessageMap.get(id);
                lock.lock();
                if (!gameStateService.checkGameExists(id)) {
                    throw new Exception("Game does not exist");
                }
                if (!gameStateService.validateToken(id, token, currentPlayer)) {
                    throw new AuthenticationException();
                }
                AbstractMap.SimpleEntry<Long, GameState.CurrentPlayer> playerEntry;
                if (currentPlayer == GameState.CurrentPlayer.FIRSTPLAYER) {
                    playerEntry = new AbstractMap.SimpleEntry<>(id, GameState.CurrentPlayer.SECONDPLAYER);
                } else {
                    playerEntry = new AbstractMap.SimpleEntry<>(id, GameState.CurrentPlayer.FIRSTPLAYER);
                }
                var res = messages.get(playerEntry).poll();
                var condition = conditionMessageMap.get(id);
                while (res == null) {
                    condition.await();
                    logger.info("-------------------Woke up from sleep " + currentPlayer);
                    res = messages.get(playerEntry).poll();
                }
                logger.info("Got message");
                output.setResult(ResponseEntity.ok(res));
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET message request: " + e.getMessage());
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET message request: " + e.getMessage());
            } finally {
                lock.unlock();
            }
            logger.info("Thread freed");
        });

        return output;
    }
}
