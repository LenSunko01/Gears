package com.example.demo.service.gamestate;

import com.example.demo.dao.gamestate.dto.GameStateRecord;
import com.example.demo.models.dto.GameState;
import com.example.demo.models.dto.User;
import com.example.demo.service.gamestaterecord.GameStateRecordService;
import com.example.demo.service.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameStateServiceImpl implements GameStateService {

    private final UserService userService;
    private final GameStateRecordService gameStateRecordService;

    public GameStateServiceImpl(UserService userService, GameStateRecordService gameStateRecordService) {
        this.userService = userService;
        this.gameStateRecordService = gameStateRecordService;
    }

    @Override
    public GameState getStateById(Long id) {
        //get record
        GameStateRecord record = gameStateRecordService.getById(id);
        List<User> users = getUsers(record);
        return new GameState(id, users);
    }

    @Override
    public GameState getStateByUserId(Long id) {
        GameStateRecord record = gameStateRecordService.getByUserId(id);
        List<User> users = getUsers(record);
        return new GameState(id, users);
    }

    @Override
    public List<GameState> getAll() {
        List<GameStateRecord>  records = gameStateRecordService.getAll();
        return records.stream()
                .map(record -> new GameState(record.getId(), getUsers(record)))
                .collect(Collectors.toList());
    }

    private List<User> getUsers(GameStateRecord record) {
        return record.getUserIds().stream()
                .map(userService::getUserById).collect(Collectors.toList());
    }
}
