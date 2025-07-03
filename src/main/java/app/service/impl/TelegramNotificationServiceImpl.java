package app.service.impl;

import app.service.TelegramBotService;
import app.service.TelegramNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationServiceImpl implements TelegramNotificationService {
    private final TelegramBotService telegramBotService;

    @Override
    public void sendNotification(String chatId, String message) {
        try {
            telegramBotService.sendMessage(Long.valueOf(chatId), message);
        } catch (Exception e) {
            throw new RuntimeException("Cannot send notification ", e);
        }
    }
}
