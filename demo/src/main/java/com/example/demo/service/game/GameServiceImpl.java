package com.example.demo.service.game;

import com.example.demo.models.dto.GameState;
import com.example.demo.models.dto.User;
import com.example.demo.service.gamestate.GameStateService;
import org.springframework.stereotype.Service;

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
        GameState bufferGame = new GameState();

        GameState newGame = gameStateService.saveGameState(bufferGame);
        long idGame = newGame.getId();
        gameStateService.addPlayersToGame(idGame, idFirstPlayer, idSecondPlayer);
        return idGame;
    }
}
