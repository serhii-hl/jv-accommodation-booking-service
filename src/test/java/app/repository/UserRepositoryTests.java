package app.repository;

import app.model.User;
import app.util.UserUtils;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/user/add-user-to-test-db.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/user/remove-user-from-test-db.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Find user by email")
    void findOptionalUserByEmail() {
        Optional<User> actual = userRepository.findByEmail("owner@example.com");
        Optional<User> expected = Optional.of(UserUtils.createExpectedTestUserOwner());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Check if user exists by email")
    void existsByEmail() {
        boolean actual = userRepository.existsByEmail("owner@example.com");
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("Check if user exists by email - false")
    void notExistsByEmail() {
        boolean actual = userRepository.existsByEmail("false@test.com");
        Assertions.assertFalse(actual);
    }

    @Test
    @DisplayName("Find user by telegram chat id")
    void findOptionalUserByChatId() {
        Optional<User> actual = userRepository.findByTelegramChatId("123456789");
        Optional<User> expected = Optional.of(UserUtils.createExpectedTestUserOwner());
        Assertions.assertEquals(expected, actual);
    }
}
