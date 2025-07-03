package app.service.impl;

import app.exception.InvalidTelegramTokenException;
import app.exception.LinkingCompletedException;
import app.model.TelegramLinkingToken;
import app.model.User;
import app.repository.TelegramLinkingTokenRepository;
import app.repository.UserRepository;
import app.service.TelegramAccountLinkingService;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TelegramAccountLinkingServiceImpl implements TelegramAccountLinkingService {
    private final TelegramLinkingTokenRepository telegramLinkingTokenRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public String generateLinkingTokenForUser(User user) {
        Optional<TelegramLinkingToken> existingToken =
                telegramLinkingTokenRepository.findByUserIdAndIsUsedFalseAndExpiresAtAfter(
                        user.getId(), LocalDateTime.now());
        if (existingToken.isPresent()) {
            return existingToken.get().getToken();
        }
        SecureRandom random = new SecureRandom();
        byte[] tokenBytes = new byte[16];
        random.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().encodeToString(tokenBytes);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(20);
        TelegramLinkingToken linkingToken = new TelegramLinkingToken(token, user, expiresAt);
        telegramLinkingTokenRepository.save(linkingToken);
        return token;
    }

    @Override
    @Transactional(noRollbackFor = {LinkingCompletedException.class})
    public void processIncomingLinkingToken(
            String receivedToken, String telegramUserId, String telegramChatId) {

        TelegramLinkingToken telegramLinkingToken =
                getTelegramLinkingToken(receivedToken);

        checkUsedToken(telegramLinkingToken);

        checkExpiredToken(telegramLinkingToken);

        User userToLink = telegramLinkingToken.getUser();
        Optional<User> existingUserWithThisTelegramId =
                userRepository.findByTelegramChatId(telegramChatId);

        if (existingUserWithThisTelegramId.isPresent()) {
            User alreadyLinkedUser = existingUserWithThisTelegramId.get();
            if (alreadyLinkedUser.getId() == userToLink.getId()) {
                if (!telegramChatId.equals(alreadyLinkedUser.getTelegramChatId())) {
                    alreadyLinkedUser.setTelegramChatId(telegramChatId);
                    userRepository.save(alreadyLinkedUser);
                }
                telegramLinkingToken.setUsed(true);
                telegramLinkingTokenRepository.save(telegramLinkingToken);
                throw new LinkingCompletedException(
                        "Your account has been linked already. No action required.");
            } else {
                telegramLinkingToken.setUsed(true);
                telegramLinkingTokenRepository.save(telegramLinkingToken);
                throw new RuntimeException(
                        "Your Telegram account is already linked to another user. "
                                + "Please contact support.");
            }
        } else {
            userToLink.setTelegramChatId(telegramChatId);
            userToLink.setTelegramUserId(telegramUserId);
            userRepository.save(userToLink);
            telegramLinkingToken.setUsed(true);
            telegramLinkingTokenRepository.save(telegramLinkingToken);
            throw new LinkingCompletedException("Great! Your account is connected!");
        }
    }

    private TelegramLinkingToken getTelegramLinkingToken(String token) {
        return telegramLinkingTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTelegramTokenException(
                        "Telegram linking token is not valid."));
    }

    private void checkUsedToken(TelegramLinkingToken telegramLinkingToken) {
        if (telegramLinkingToken.isUsed()) {
            throw new InvalidTelegramTokenException("Telegram linking token is used.");
        }
    }

    private void checkExpiredToken(TelegramLinkingToken telegramLinkingToken) {
        if (telegramLinkingToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            telegramLinkingToken.setUsed(true);
            telegramLinkingTokenRepository.save(telegramLinkingToken);
            throw new InvalidTelegramTokenException("Telegram linking token is expired.");
        }
    }
}
