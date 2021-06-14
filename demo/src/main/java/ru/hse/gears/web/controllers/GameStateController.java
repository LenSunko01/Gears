package ru.hse.gears.web.controllers;

import ru.hse.gears.models.dto.Board;
import ru.hse.gears.models.dto.GameState;
import ru.hse.gears.models.dto.Message;
import ru.hse.gears.service.gamestate.GameStateService;
import ru.hse.gears.web.exceptions.AuthenticationException;
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

    @GetMapping("/game/{id}/player/{currentPlayer}")
    DeferredResult<ResponseEntity<GameState>> gameState(
            @RequestHeader HttpHeaders headers,
            @PathVariable Long id,
            @PathVariable GameState.CurrentPlayer currentPlayer
    ) {
        logger.info("--------------> Received GET game request with id " + id + " and player " + currentPlayer);
        var token = headers.getFirst("token");
        DeferredResult<ResponseEntity<GameState>> output = new DeferredResult<>(ControllersConstants.getGameTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("GET game by ID request completed for player " + currentPlayer));
        output.onTimeout(() -> {
            logger.info("TIMEOUT: Timeout during executing GET game request with id " + id + " and player " + currentPlayer);
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing GET game request with id " + id + " and player " + currentPlayer + " in separate thread");
            Lock lock = new ReentrantLock();
            try {
                lock = lockGameMap.get(id);
                lock.lock();
                var res = gameStateService.getStateById(id, token, currentPlayer);
                while (!(res.getCurrentPlayer().equals(currentPlayer)
                        && res.isFirstPlayerHasInitializedBoard()
                        && res.isSecondPlayerHasInitializedBoard())
                ) {
                    logger.info("GET game request with id " + id + " and player " + currentPlayer + "; wait for some time");
                    var condition = conditionGameChangedMap.get(id);
                    condition.await();
                    res = gameStateService.getStateById(id, token, currentPlayer);
                }
                logger.info("GET game request with id " + id + " and player " + currentPlayer + "; received game");
                output.setResult(ResponseEntity.ok(res));
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET game request with id " + id + " and player "
                        + currentPlayer + " : " + e.getMessage());
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET game request with id " + id + " and player "
                        + currentPlayer + " : " + e.getMessage());
            } finally {
                lock.unlock();
            }
            logger.info("Thread freed from GET game request with id " + id + " and player " + currentPlayer);
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
        logger.info("--------------> Received POST game request with id " + id + " and player " + currentPlayer);
        var token = headers.getFirst("token");
        DeferredResult<ResponseEntity<String>> output = new DeferredResult<>(ControllersConstants.postGameTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("POST game by ID request completed for player " + currentPlayer));
        output.onTimeout(() -> {
            logger.info("TIMEOUT: Timeout during executing POST game request with id " + id + " and player " + currentPlayer);
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing POST game request with id " + id + " and player " + currentPlayer + " in separate thread");
            Lock lock = new ReentrantLock();
            try {
                lock = lockGameMap.get(id);
                lock.lock();
                gameStateService.updateStateById(id, token, newGameState, currentPlayer);
                output.setResult(ResponseEntity.ok("Game updated"));
                logger.info("Game updated in POST game request with id " + id + " and player " + currentPlayer);
                conditionGameChangedMap.get(id).signalAll();
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing POST game request with id " + id + " and player "
                        + currentPlayer + " : " + e.getMessage());
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing POST game request with id " + id + " and player "
                        + currentPlayer + " : " + e.getMessage());
            } finally {
                lock.unlock();
            }
            logger.info("Thread freed from POST game request with id " + id + " and player " + currentPlayer);
        });

        return output;
    }

    @GetMapping("/board/{id}")
    DeferredResult<ResponseEntity<GameState>> getBoardFromFirstPlayer(
            @RequestHeader HttpHeaders headers,
            @PathVariable Long id
    ) {
        logger.info("--------------> Received GET board request with id " + id);
        var token = headers.getFirst("token");
        DeferredResult<ResponseEntity<GameState>> output = new DeferredResult<>(ControllersConstants.getGameTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("GET board by ID request completed for id " + id));
        output.onTimeout(() -> {
            logger.info("TIMEOUT: Timeout during executing GET board request with id " + id);
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing GET board request with id " + id + " in separate thread");
            onGameCreation(id);
            Lock lock = new ReentrantLock();
            try {
                lock = lockGameMap.get(id);
                lock.lock();
                var res = gameStateService.getStateById(id, token, GameState.CurrentPlayer.SECONDPLAYER);
                while (!res.isFirstPlayerHasInitializedBoard()) {
                    logger.info("GET board request with id " + id + "; wait for some time");
                    var condition = conditionInitMap.get(id);
                    condition.await();
                    res = gameStateService.getStateById(id, token, GameState.CurrentPlayer.SECONDPLAYER);
                }
                logger.info("GET board request with id " + id + "; received board");
                output.setResult(ResponseEntity.ok(res));
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET board request with id " + id + " : " + e.getMessage());
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET board request with id " + id + " : " + e.getMessage());
            } finally {
                lock.unlock();
            }
            logger.info("Thread freed from GET board request with id " + id);
        });
        return output;
    }
    //TODO: перенос в сервис??
    private void onGameCreation(Long id) {
        logger.info("In onGameCreation metod with id " + id);
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
        logger.info("Completed onGameCreation with id " + id);
    }

    @PostMapping("/init/{id}/player/{currentPlayer}")
    DeferredResult<ResponseEntity<GameState>> initBoard(
            @RequestHeader HttpHeaders headers,
            @PathVariable Long id,
            @PathVariable GameState.CurrentPlayer currentPlayer,
            @RequestBody Board board
    ) {
        logger.info("--------------> Received POST init game request with id " + id + " and player " + currentPlayer);
        var token = headers.getFirst("token");
        DeferredResult<ResponseEntity<GameState>> output = new DeferredResult<>(ControllersConstants.initGameTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("POST init game request completed for player " + currentPlayer));
        output.onTimeout(() -> {
            logger.info("TIMEOUT: Timeout during executing POST init game request with id " + id + " and player " + currentPlayer);
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing POST init game request with id " + id + " and player " + currentPlayer + " in separate thread");
            onGameCreation(id);
            Lock lock = new ReentrantLock();
            try {
                lock = lockGameMap.get(id);
                lock.lock();
                var res = gameStateService.updateBoardById(id, token, currentPlayer, board);
                output.setResult(ResponseEntity.ok(res));
                conditionInitMap.get(id).signalAll();
                conditionGameChangedMap.get(id).signalAll();
                logger.info("Completed POST init game request with id " + id + " and player " + currentPlayer);
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing POST init game request with id " + id + " and player "
                        + currentPlayer + " : " + e.getMessage());
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing POST init game request with id " + id + " and player "
                        + currentPlayer + " : " + e.getMessage());
            } finally {
                lock.unlock();
            }
            logger.info("Thread freed from POST init game request with id " + id + " and player " + currentPlayer);
        });

        return output;
    }

    private void onGameDeletion(Long id) {
        logger.info("In onGameDeletion method with id " + id);
        var lock = lockGameMap.get(id);
        if (lock == null) {
            logger.info("Return from onGameDeletion method with id " + id);
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
        logger.info("Completed onGameDeletion method with id " + id);
    }

    @DeleteMapping("/game/{id}/player/{currentPlayer}")
    DeferredResult<ResponseEntity<String>> deleteGame(
            @RequestHeader HttpHeaders headers,
            @PathVariable Long id,
            @PathVariable GameState.CurrentPlayer currentPlayer
    ) {
        logger.info("--------------> Received DELETE game request with id " + id + " and player " + currentPlayer);
        var token = headers.getFirst("token");
        DeferredResult<ResponseEntity<String>> output = new DeferredResult<>(ControllersConstants.deleteGameTimeoutInMilliseconds);
        output.onCompletion(() -> {
            onGameDeletion(id);
            logger.info("DELETE game request completed for player " + currentPlayer);
        });
        output.onTimeout(() -> {
            onGameDeletion(id);
            logger.info("TIMEOUT: Timeout during executing DELETE game request with id " + id + " and player " + currentPlayer);
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing DELETE game request with id " + id + " and player " + currentPlayer + " in separate thread");
            try {
                gameStateService.deleteGameState(id, token, currentPlayer);
                output.setResult(ResponseEntity.ok("Game deleted"));
                logger.info("Completed DELETE game request with id " + id + " and player " + currentPlayer);
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing DELETE game request with id " + id + " and player "
                        + currentPlayer + " : " + e.getMessage());
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing DELETE game request: " + e.getMessage());
            }

            logger.info("Thread freed from DELETE game request with id " + id + " and player " + currentPlayer);
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
        logger.info("--------------> Received POST message request with id " + id + " and player " + currentPlayer);
        var token = headers.getFirst("token");
        DeferredResult<ResponseEntity<String>> output = new DeferredResult<>(ControllersConstants.postMessageTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("POST message request completed for player " + currentPlayer));
        output.onTimeout(() -> {
            logger.info("TIMEOUT: Timeout during executing POST message request with id " + id + " and player " + currentPlayer);
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing POST message request with id " + id + " and player " + currentPlayer + " in separate thread");
            Lock lock = new ReentrantLock();
            try {
                lock = lockMessageMap.get(id);
                lock.lock();
                if (!gameStateService.checkGameExists(id)) {
                    logger.info("Exception in POST message request with id " + id + " : game does not exist");
                    throw new Exception("Game does not exist");
                }
                if (!gameStateService.validateToken(id, token, currentPlayer)) {
                    logger.info("Exception in POST message request with id " + id + " : could not validate token");
                    throw new AuthenticationException();
                }
                var playerEntry = new AbstractMap.SimpleEntry<>(id, currentPlayer);
                messages.get(playerEntry).add(message);
                var condition = conditionMessageMap.get(id);
                condition.signalAll();
                output.setResult(ResponseEntity.ok("Message sent"));
                logger.info("Completed POST message request with id " + id);
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing POST message request with id " + id + " and player "
                        + currentPlayer + " : " + e.getMessage());
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing POST message request with id " + id + " and player "
                        + currentPlayer + " : " + e.getMessage());
            } finally {
                lock.unlock();
            }

            logger.info("Thread freed from POST message request with id " + id + " and player " + currentPlayer);
        });

        return output;
    }

    @GetMapping("/message/{id}/player/{currentPlayer}")
    DeferredResult<ResponseEntity<Message>> getMessage(
            @RequestHeader HttpHeaders headers,
            @PathVariable Long id,
            @PathVariable GameState.CurrentPlayer currentPlayer
    ) {
        logger.info("--------------> Received GET message request with id " + id + " and player " + currentPlayer);
        var token = headers.getFirst("token");
        DeferredResult<ResponseEntity<Message>> output = new DeferredResult<>(ControllersConstants.getMessageTimeoutInMilliseconds);
        output.onCompletion(() -> logger.info("GET message request completed for player " + currentPlayer));
        output.onTimeout(() -> {
            logger.info("TIMEOUT: Timeout during executing GET message request with id " + id + " and player " + currentPlayer);
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout occurred."));
        });

        ForkJoinPool.commonPool().submit(() -> {
            logger.info("Processing GET message request with id " + id + " and player " + currentPlayer + " in separate thread");
            Lock lock = new ReentrantLock();
            try {
                lock = lockMessageMap.get(id);
                lock.lock();
                if (!gameStateService.checkGameExists(id)) {
                    logger.info("Exception in POST message request with id " + id + " : game does not exist");
                    throw new Exception("Game does not exist");
                }
                if (!gameStateService.validateToken(id, token, currentPlayer)) {
                    logger.info("Exception in POST message request with id " + id + " : could not validate token");
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
                    logger.info("GET message request with id " + id + " and player " + currentPlayer + "; wait for some time");
                    condition.await();
                    res = messages.get(playerEntry).poll();
                }
                logger.info("Completed GET message request with id " + id);
                output.setResult(ResponseEntity.ok(res));
            } catch (AuthenticationException e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET message request with id " + id + " and player "
                        + currentPlayer + " : " + e.getMessage());
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage()));
                logger.info("Exception while executing GET message request with id " + id + " and player "
                        + currentPlayer + " : " + e.getMessage());
            } finally {
                lock.unlock();
            }
            logger.info("Thread freed from GET message request with id " + id + " and player " + currentPlayer);
        });

        return output;
    }
}
