package app.repository;

import app.model.TelegramLinkingToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TelegramLinkingTokenRepository
        extends JpaRepository<TelegramLinkingToken, Long>,
        JpaSpecificationExecutor<TelegramLinkingToken> {
    @Query("SELECT t FROM TelegramLinkingToken t JOIN FETCH t.user WHERE t.token = :token")
    Optional<TelegramLinkingToken> findByToken(@Param("token") String token);

    Optional<TelegramLinkingToken>
                findByUserIdAndIsUsedFalseAndExpiresAtAfter(
                        Long userId, java.time.LocalDateTime now);

}
