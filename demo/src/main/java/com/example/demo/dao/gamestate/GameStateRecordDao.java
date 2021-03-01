package com.example.demo.dao.gamestate;

import com.example.demo.dao.gamestate.dto.GameStateRecord;

import java.util.List;

public interface GameStateRecordDao {
    GameStateRecord getStateRecordById(Long id);
    GameStateRecord getStateRecordByUserId(Long id);
    GameStateRecord saveStateRecord(GameStateRecord record);
    List<GameStateRecord> getAll();
}
