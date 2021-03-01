package com.example.demo.dao.gamestate;

import com.example.demo.dao.gamestate.dto.GameStateRecord;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/*
Remember about concurrency (!)
 */


@Repository
public class GameStateRecordDaoImpl implements com.example.demo.dao.gamestate.GameStateRecordDao {

    Map<Long, GameStateRecord> storage = new HashMap<>();



    @Override
    public GameStateRecord getStateRecordById(Long id) {
        return storage.get(id);
    }

    @Override
    public GameStateRecord getStateRecordByUserId(Long id) {
        return storage.values()
                .stream()
                .filter(gameStateRecord -> gameStateRecord.getUserIds().contains(id))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    @Override
    public GameStateRecord saveStateRecord(GameStateRecord record) {
        //generate id
        long id = new Random().nextLong();
        while (storage.containsKey(id)) {
            id = new Random().nextLong();
        }
        record.setId(id);
        storage.put(record.getId(), record);
        return record;
    }

    @Override
    public List<GameStateRecord> getAll() {
        return new ArrayList<>(storage.values());
    }

}
