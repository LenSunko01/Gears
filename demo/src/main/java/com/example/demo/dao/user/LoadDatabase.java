package com.example.demo.dao.user;

import com.example.demo.dao.gamestate.GameStateDao;

import com.example.demo.dao.gamestate.GameStateDaoImpl;
import com.example.demo.models.dto.User;
import com.example.demo.service.game.GameService;
import com.example.demo.service.game.GameServiceImpl;
import com.example.demo.service.gamestate.GameStateService;
import com.example.demo.service.gamestate.GameStateServiceImpl;
import com.example.demo.service.user.UserService;
import com.example.demo.service.user.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(UserRepository repository, GameStateDao gameStateDao) {

        return args -> {
            log.info("Preloading " + repository.save(new User("Player X", 87L)));
            log.info("Preloading " + repository.save(new User("Player Y", 92L)));
            log.info("Preloading " + repository.save(new User("Player Z", 34L)));
            log.info("Preloading " + repository.save(new User("Player N", 29L)));
            log.info("Preloading " + repository.save(new User("Player M", 101L)));

            User testUser1 = repository.save(new User("Test 1", 101L));
            User testUser2 = repository.save(new User("Test 2", 202L));

            UserService user = new UserServiceImpl(repository);
            GameStateDaoImpl impl = new GameStateDaoImpl();
            GameStateService service = new GameStateServiceImpl(impl);
            GameService gameService = new GameServiceImpl(service, user);
            gameService.setGame(testUser1,testUser2);
           // GameStateRecord record = new GameStateRecord(List.of(testUser1.getId(), testUser2.getId()));

          //  record = gameStateRecordDao.saveStateGame(record);

            log.info("LOAD GAMESTATE with id " + gameService.setGame(testUser1,testUser2));

        };
    }
}