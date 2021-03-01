package com.example.demo.dao.user;

import com.example.demo.dao.gamestate.GameStateRecordDao;
import com.example.demo.dao.gamestate.dto.GameStateRecord;
import com.example.demo.models.dto.User;
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
    CommandLineRunner initDatabase(UserRepository repository, GameStateRecordDao gameStateRecordDao) {

        return args -> {
            log.info("Preloading " + repository.save(new User("Player X", 87L)));
            log.info("Preloading " + repository.save(new User("Player Y", 92L)));
            log.info("Preloading " + repository.save(new User("Player Z", 34L)));
            log.info("Preloading " + repository.save(new User("Player N", 29L)));
            log.info("Preloading " + repository.save(new User("Player M", 101L)));

            User testUser1 = repository.save(new User("Test 1", 101L));
            User testUser2 = repository.save(new User("Test 2", 202L));

            GameStateRecord record = new GameStateRecord(List.of(testUser1.getId(), testUser2.getId()));

            record = gameStateRecordDao.saveStateRecord(record);

            log.info("LOAD GAMESTATE with id " + record.getId());

        };
    }
}