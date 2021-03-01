package com.example.demo.dao.gamestate;

import com.example.demo.models.dto.GameState;

import java.util.List;

public interface GameStateDao {
    GameStateRecord getById(Long id);
    GameStateRecord getByUserId(Long id);
    List<GameStateRecord> getAll();

}
