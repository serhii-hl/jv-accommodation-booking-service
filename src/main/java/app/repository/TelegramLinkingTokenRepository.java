package app.repository;

import app.model.TelegramLinkingToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TelegramLinkingTokenRepository
        extends JpaRepository<TelegramLinkingToken, Long>,
        JpaSpecificationExecutor<TelegramLinkingToken> {
    Optional<TelegramLinkingToken> findByToken(String token);

    Optional<TelegramLinkingToken>
                findByUserIdAndIsUsedFalseAndExpiresAtAfter(
                        Long userId, java.time.LocalDateTime now);

}
