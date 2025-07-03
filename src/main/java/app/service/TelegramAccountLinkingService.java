package app.service;

import app.model.User;

public interface TelegramAccountLinkingService {
    String generateLinkingTokenForUser(User user);

    void processIncomingLinkingToken(
            String receivedToken, String telegramUserId, String telegramChatId);
}
