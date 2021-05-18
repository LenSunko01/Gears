package com.example.demo.web.controllers;

import com.example.demo.models.dto.Board;
import com.example.demo.models.dto.GameState;
import com.example.demo.service.gamestate.GameStateService;
import com.example.demo.web.exceptions.AuthenticationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class GameStateController {
    private static final Log logger = LogFactory.getLog(UserController.class);
    private final GameStateService gameStateService;

    public GameStateController(GameStateService gameStateService) {
        this.gameStateService = gameStateService;
    }

    private ResponseEntity<GameState> keepPolling(
            Long id, String token, GameState.CurrentPlayer currentPlayer
    ) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ignored) {
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/get-game?id=" + id + "&token=" + token + "&currentPlayer=" + currentPlayer));
        return new ResponseEntity<>(headers, HttpStatus.TEMPORARY_REDIRECT);
    }

    @PostMapping("/get-game")
    ResponseEntity<GameState> gameState(
            @RequestParam Long id, @RequestParam String token, @RequestParam GameState.CurrentPlayer currentPlayer
    ) {
        logger.info("Received get game state by ID request");
        GameState res;
        try {
            res = gameStateService.getStateById(id, token);
        } catch (AuthenticationException e) {
            logger.info("Caught authentication exception");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (res.getCurrentPlayer().equals(currentPlayer)
            && res.isFirstPlayerHasInitializedBoard()
            && res.isSecondPlayerHasInitializedBoard()
        ) {
            return ResponseEntity.ok(res);
        }
        return keepPolling(id, token, currentPlayer);
    }

    @PostMapping("/update-game/{id}")
    ResponseEntity<String> updateGameState(
            @RequestHeader HttpHeaders headers, @PathVariable Long id, @RequestBody GameState newGameState
    ) {
        var token = headers.getFirst("userToken");
        logger.info("Received update game state by ID request");
        try {
            gameStateService.updateStateById(id, token, newGameState);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("No right to update the game", HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok("Game updated");
    }

    @PostMapping("/init")
    ResponseEntity<GameState> initBoard(
            @RequestParam Long id,
            @RequestParam String token,
            @RequestParam GameState.CurrentPlayer currentPlayer,
            @RequestBody Board board
    ) {
        logger.info("Received init board in game state by ID request");
        GameState res;
        try {
            res = gameStateService.updateBoardById(id, token, currentPlayer, board);
        } catch (AuthenticationException e) {
            logger.info("Caught authentication exception");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/end-game")
    ResponseEntity<String> deleteGame(@RequestParam Long id, @RequestParam String token) {
        logger.info("Received end-game by ID request");
        try {
            gameStateService.deleteGameState(id, token);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("No right to delete the game", HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok("Game deleted");
    }
}
