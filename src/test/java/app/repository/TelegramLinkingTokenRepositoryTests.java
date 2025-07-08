package app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import app.model.TelegramLinkingToken;
import app.util.TelegramLinkingTokenUtils;
import java.time.LocalDateTime;
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
@Sql(scripts = "classpath:database/telegramLinkingToken/add-token-to-test-db.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/telegramLinkingToken/remove-token-from-test-db.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class TelegramLinkingTokenRepositoryTests {
    @Autowired
    private TelegramLinkingTokenRepository telegramLinkingTokenRepository;

    @Test
    @DisplayName("Find all TG linking token by token")
    void findLinkingTokenByToken() {
        String token = "test token";
        Optional<TelegramLinkingToken> expected = Optional.of(
                TelegramLinkingTokenUtils.createExpectedTelegramLinkingToken());
        Optional<TelegramLinkingToken> actual = telegramLinkingTokenRepository
                .findByToken(token);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Find by user id, used status and expiration date")
    void findByUserIdAndIsUsedFalseAndExpiresAtAfter() {
        TelegramLinkingToken expectedToken = TelegramLinkingTokenUtils
                .createExpectedTelegramLinkingToken();
        Long userId = 100L;
        LocalDateTime requestedExpiresAt = LocalDateTime.of(2025, 7,
                6, 23, 59, 59);
        Optional<TelegramLinkingToken> actual = telegramLinkingTokenRepository
                .findByUserIdAndIsUsedFalseAndExpiresAtAfter(userId, requestedExpiresAt);
        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(expectedToken);
    }

    @Test
    @DisplayName("Can`t find by user id, used status as token expired")
    void findByUserIdAndIsUsedFalseAndExpiresBefore() {
        Long userId = 100L;
        LocalDateTime requestedExpiresAt = LocalDateTime.of(2025, 7,
                7, 23, 59, 59);
        Optional<TelegramLinkingToken> actual = telegramLinkingTokenRepository
                .findByUserIdAndIsUsedFalseAndExpiresAtAfter(userId, requestedExpiresAt);
        assertThat(actual).isNotPresent();
    }
}
