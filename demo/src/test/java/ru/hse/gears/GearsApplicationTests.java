package ru.hse.gears;

import ru.hse.gears.web.controllers.GameStateController;
import ru.hse.gears.web.controllers.UserController;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GearsApplicationTests {
    @Autowired
    private UserController userController;

    @Autowired
    private GameStateController gameStateController;

	@Test
	void contextLoads() {
        assertThat(userController).isNotNull();
        assertThat(gameStateController).isNotNull();
	}

}
