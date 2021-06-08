package com.example.demo.web.controllers;

import com.example.demo.models.dto.Board;
import com.example.demo.models.dto.GameState;
import com.example.demo.models.dto.Message;
import com.example.demo.models.dto.User;
import com.example.demo.service.gamestate.GameStateService;
import com.example.demo.web.exceptions.AuthenticationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.coyote.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.AbstractMap;
import java.util.concurrent.*;

import static com.example.demo.web.controllers.ControllersConstants.*;

@RestController
public class GameStateController {
    private static final Log logger = LogFactory.getLog(GameStateController.class);
    private final GameStateService gameStateService;
    ConcurrentHashMap<AbstractMap.SimpleEntry<Long, GameState.CurrentPlayer>, ConcurrentLinkedQueue<Message>> messages = new ConcurrentHashMap<>();
    public GameStateController(GameStateService gameStateService) {
        this.gameStateService = gameStateService;
    }

    private final ExecutorService workerPool = Executors.newFixedThreadPool(5);

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

        logger.info(Thread.currentThread().getId());

        workerPool.submit(() -> {
            logger.info("Processing in separate thread");
            try {
                logger.info(Thread.currentThread().getId());
                logger.info("Trying to get game state, before while");
                logger.info(currentPlayer);
                var res = gameStateService.getStateById(id, token);
                while (!(res.getCurrentPlayer().equals(currentPlayer)
                        && res.isFirstPlayerHasInitializedBoard()
                        && res.isSecondPlayerHasInitializedBoard())
                ) {

                    logger.info(Thread.currentThread().getId());
                    logger.info(res.getCurrentPlayer());
                    logger.info(res.isFirstPlayerHasInitializedBoard());
                    logger.info(res.isSecondPlayerHasInitializedBoard());
                    logger.info(currentPlayer);
                    logger.info("Game not ready yet, wait for some time " + currentPlayer);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignored) {
                        logger.info("------------------------------Exception-------------------", ignored);
                    }
                    logger.info("-------------------Woke up from sleep " + currentPlayer);
                    res = gameStateService.getStateById(id, token);
                    logger.info(output.isSetOrExpired());
                    if (output.isSetOrExpired()) {
                        logger.info("----------Request expired");
                        break;
                    }
                }
                logger.info("Set game");
                output.setResult(ResponseEntity.ok(res));
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET game request: " + e.getMessage());
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

        workerPool.submit(() -> {
            logger.info("Processing in separate thread");
            try {
                gameStateService.updateStateById(id, token, newGameState);
                output.setResult(ResponseEntity.ok("Game updated"));
                logger.info("Game updated");
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET user request: " + e.getMessage());
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

        workerPool.submit(() -> {
            logger.info("Processing in separate thread");
            try {
                var res = gameStateService.getStateById(id, token);
                while (!res.isFirstPlayerHasInitializedBoard()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignored) {
                        logger.info("------------------------------Exception-------------------", ignored);
                    }
                    res = gameStateService.getStateById(id, token);
                    if (output.isSetOrExpired()) {
                        logger.info("----------Request expired");
                        break;
                    }
                }
                output.setResult(ResponseEntity.ok(res));
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET board request: " + e.getMessage());
            }
            logger.info("Thread freed");
        });
        return output;
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

        workerPool.submit(() -> {
            logger.info("Processing in separate thread");
            try {
                var res = gameStateService.updateBoardById(id, token, currentPlayer, board);
                logger.info(res.getFirstPlayerBoard().equals(board));
                output.setResult(ResponseEntity.ok(res));
                logger.info("Completed POST init game request");
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing POST init game request: " + e.getMessage());
            }
            logger.info("Thread freed");
        });

        return output;
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
        output.onCompletion(() -> logger.info("DELETE game by ID request completed"));
        output.onTimeout(() -> {
            logger.info("Timeout during executing DELETE game request");
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        workerPool.submit(() -> {
            logger.info("Processing in separate thread");
            try {
                gameStateService.deleteGameState(id, token);
                messages.remove(new AbstractMap.SimpleEntry<>(id, currentPlayer));
                output.setResult(ResponseEntity.ok("Game deleted"));
                logger.info("Completed DELETE game request");
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
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
            try {
                if (!gameStateService.checkGameExists(id)) {
                    throw new Exception("Game does not exist");
                }
                gameStateService.validateToken(id, token);
                var playerEntry = new AbstractMap.SimpleEntry<>(id, currentPlayer);
                messages.putIfAbsent(playerEntry, new ConcurrentLinkedQueue<>());
                messages.get(playerEntry).add(message);
                output.setResult(ResponseEntity.ok("Message sent"));
                logger.info("Completed POST message request");
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing POST message request: " + e.getMessage());
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
            try {
                if (!gameStateService.checkGameExists(id)) {
                    throw new Exception("Game does not exist");
                }
                gameStateService.validateToken(id, token);
                AbstractMap.SimpleEntry<Long, GameState.CurrentPlayer> playerEntry;
                if (currentPlayer == GameState.CurrentPlayer.FIRSTPLAYER) {
                    playerEntry = new AbstractMap.SimpleEntry<>(id, GameState.CurrentPlayer.SECONDPLAYER);
                } else {
                    playerEntry = new AbstractMap.SimpleEntry<>(id, GameState.CurrentPlayer.FIRSTPLAYER);
                }
                messages.putIfAbsent(playerEntry, new ConcurrentLinkedQueue<>());
                var res = messages.get(playerEntry).poll();
                while (res == null) {
                    logger.info("No messages yet, wait for some time " + currentPlayer);
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException ignored) {
                        logger.info("------------------------------Exception-------------------", ignored);
                    }
                    logger.info("-------------------Woke up from sleep " + currentPlayer);
                    res = messages.get(playerEntry).poll();
                    if (output.isSetOrExpired()) {
                        logger.info("----------Request expired");
                        break;
                    }
                }
                logger.info("Got message");
                output.setResult(ResponseEntity.ok(res));
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET message request: " + e.getMessage());
            }
            logger.info("Thread freed");
        });

        return output;
    }
}
