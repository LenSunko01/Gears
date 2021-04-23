package com.example.demo.service.gamestate;

import com.example.demo.models.dto.GameState;
import com.example.demo.models.dto.User;

import java.util.List;

public interface GameStateService {
    Long setGame(User firstUser, User secondUser);
    GameState getStateById(Long id, String token);
    GameState updateStateById(Long id, String token, GameState newGameState);
    void deleteGameState(Long id, String token);
}
