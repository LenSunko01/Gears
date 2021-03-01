package com.example.demo.service.gamestaterecord;

import com.example.demo.dao.gamestate.dto.GameStateRecord;

import java.util.List;

public interface GameStateRecordService {
    GameStateRecord getById(Long id);
    GameStateRecord getByUserId(Long id);
    List<GameStateRecord> getAll();
}
