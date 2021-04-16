package com.example.demo.service.game;

import com.example.demo.models.dto.GameState;
import com.example.demo.models.dto.User;
import com.example.demo.service.gamestate.GameStateService;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class GameServiceImpl implements GameService {
    private final GameStateService gameStateService;

    public GameServiceImpl(GameStateService gameStateService) {
        this.gameStateService = gameStateService;
    }

    @Override
    public Long setGame(User firstUser, User secondUser) {
        long idFirstPlayer = firstUser.getId();
        long idSecondPlayer = secondUser.getId();
        GameState bufferGame = new GameState(Arrays.asList(firstUser, secondUser));

        GameState newGame = gameStateService.saveGameState(bufferGame);
        long idGame = newGame.getId();
        gameStateService.addPlayersToGame(idGame, idFirstPlayer, idSecondPlayer);
        return idGame;
    }

    @Override
    public void endGame(GameState gameState) {
        var buffer = gameState.getUsers();
        var firstUser = buffer.get(0);
        var secondUser = buffer.get(1);
        if (gameState.getCurrentGameState() == GameState.CurrentGameState.FIRSTPLAYER) {
            firstUser.setPoints(firstUser.getPoints() + gameState.getScoreOfFirstPlayer());
            return;
        }
        if (gameState.getCurrentGameState() == GameState.CurrentGameState.SECONDPLAYER) {
            secondUser.setPoints(secondUser.getPoints() + gameState.getScoreOfSecondPlayer());
            return;
        }
        firstUser.setPoints((firstUser.getPoints() + gameState.getScoreOfFirstPlayer()) / 2);
        secondUser.setPoints((secondUser.getPoints() + gameState.getScoreOfSecondPlayer()) / 2);
        gameStateService.deleteGame(gameState.getId());
    }
}
