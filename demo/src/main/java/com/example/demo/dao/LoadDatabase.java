package com.example.demo.dao;

import com.example.demo.models.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(UserRepository repository) {

        return args -> {
            log.info("Preloading " + repository.save(new User("Player X", 87L)));
            log.info("Preloading " + repository.save(new User("Player Y", 92L)));
            log.info("Preloading " + repository.save(new User("Player Z", 34L)));
            log.info("Preloading " + repository.save(new User("Player N", 29L)));
            log.info("Preloading " + repository.save(new User("Player M", 101L)));
        };
    }
}