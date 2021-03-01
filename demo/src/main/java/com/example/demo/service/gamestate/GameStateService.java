package com.example.demo.service.gamestate;

import com.example.demo.models.dto.GameState;

import java.util.List;

public interface GameStateService {
    GameState getById(Long id);
    List<GameState> getAll();
    GameState getByUserId(Long id);
}
