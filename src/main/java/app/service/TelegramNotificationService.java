package app.service;

public interface TelegramNotificationService {
    void sendNotification(String chatId, String message);
}
