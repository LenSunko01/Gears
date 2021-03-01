package com.example.demo.service.gamestate;

import com.example.demo.models.dto.GameState;

import java.util.List;

public interface GameStateService {
    GameState getStateById(Long id);
    GameState getStateByUserId(Long id);
    List<GameState> getAll();
}
