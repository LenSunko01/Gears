package ru.hse.gears.dao.context;
import ru.hse.gears.service.gamestate.GameStateService;
import ru.hse.gears.service.registration.RegistrationService;
import ru.hse.gears.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(
            UserService userService,
            RegistrationService registrationService,
            GameStateService gameStateService
    ) {

        return args -> {
            /*
            load context, if necessary, for example:

            var secondUser = new User(1L, "username1", "password1", 0L, 0L, 0L, 0L);
            var firstUser = new User(2L, "username2", "password2", 0L, 0L, 0L, 0L);
            var gameId = gameStateService.setGame(firstUser, secondUser);
            log.info("Game id " + gameId);
             */
        };
    }
}