package com.example.demo.service.gamestate;

import com.example.demo.dao.gamestate.GameStateDao;
import com.example.demo.dao.gamestate.GameStateRecord;
import com.example.demo.models.dto.GameState;
import com.example.demo.models.dto.User;
import com.example.demo.service.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameStateServiceImpl implements GameStateService {
    private UserService userService;
    private GameStateDao gameStateDao;

    public GameStateServiceImpl(UserService userService, GameStateDao gameStateDao) {
        this.userService = userService;
        this.gameStateDao = gameStateDao;
    }

    @Override
    public GameState getById(Long id) {
        return null;
    }

    @Override
    public List<GameState> getAll() {
        return null;
    }

    @Override
    public GameState getByUserId(Long id) {
        //step 1 get GameStateRecord
        GameStateRecord stateRecord =  gameStateDao.getByUserId(id);


        //get User objects by user ids
        List<User> users = stateRecord.getUserIds()
                .stream()
                .map(userId -> userService.getUserById(userId))
                .collect(Collectors.toList());


        //construct GameState
        return new GameState(id, users);
    }
}
