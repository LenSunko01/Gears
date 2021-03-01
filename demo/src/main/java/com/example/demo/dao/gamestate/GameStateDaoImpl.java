package com.example.demo.dao.gamestate;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GameStateDaoImpl implements GameStateDao {

    private Map<Long, GameStateRecord> store = new HashMap<>();


    @Override
    public GameStateRecord getById(Long id) {
        return store.get(id);
    }

    @Override
    public GameStateRecord getByUserId(Long id) {
        return store.values()
                .stream()
                .filter(gameStateRecord -> gameStateRecord.getUserIds().contains(id))
                .findFirst()
                .orElseThrow(IllegalStateException::new); //change to adecvate
    }

    @Override
    public List<GameStateRecord> getAll() {
        return new ArrayList<>(store.values());
    }
}
