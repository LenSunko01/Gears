package com.example.demo.service.gamestate;

import com.example.demo.models.dto.GameState;
import com.example.demo.models.dto.User;

import java.util.List;

public interface GameStateService {
    GameState getStateById(Long id);
    GameState getStateByUserId(Long id);
    void deleteGame(Long id);
    List<GameState> getAll();
    GameState saveGameState(GameState game);
    void addPlayersToGame(Long idGame, Long idUserOne, Long idUserSecond);
}
