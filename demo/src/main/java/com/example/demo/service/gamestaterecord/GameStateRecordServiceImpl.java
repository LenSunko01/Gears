package com.example.demo.service.gamestaterecord;

import com.example.demo.dao.gamestate.GameStateRecordDao;
import com.example.demo.dao.gamestate.dto.GameStateRecord;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameStateRecordServiceImpl implements  GameStateRecordService {

    private GameStateRecordDao stateRecordDao;

    public GameStateRecordServiceImpl(GameStateRecordDao stateRecordDao) {
        this.stateRecordDao = stateRecordDao;
    }

    @Override
    public GameStateRecord getById(Long id) {
        return stateRecordDao.getStateRecordById(id);
    }

    @Override
    public GameStateRecord getByUserId(Long id) {
        return stateRecordDao.getStateRecordByUserId(id);
    }

    @Override
    public List<GameStateRecord> getAll() {
        return stateRecordDao.getAll();
    }
}
