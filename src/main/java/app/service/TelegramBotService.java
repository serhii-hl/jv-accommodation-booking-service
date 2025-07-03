package app.service;

public interface TelegramBotService {

    void sendMessage(Long chatId, String text);

    String getBotUsername();
}
