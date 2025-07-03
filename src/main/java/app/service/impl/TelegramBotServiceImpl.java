package app.service.impl;

import app.exception.InvalidTelegramTokenException;
import app.exception.LinkingCompletedException;
import app.service.TelegramAccountLinkingService;
import app.service.TelegramBotService;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Service
@RequiredArgsConstructor
public class TelegramBotServiceImpl extends TelegramLongPollingBot implements TelegramBotService {

    private final TelegramAccountLinkingService telegramAccountLinkingService;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            String telegramUserId = update.getMessage().getFrom().getId().toString();

            if (messageText.startsWith("/start")) {
                String payload = "";
                if (messageText.length() > "/start".length()) {
                    payload = messageText.substring("/start".length()).trim();
                }

                if (!payload.isEmpty()) {
                    processLinkingCommand(payload, telegramUserId, chatId);
                } else {
                    sendMessage(Long.valueOf(chatId),
                            "Hi, I am bot for linking your account to our system. "
                                    + "Firstly you need to register on our website and create "
                                    + "a request for matching your profile with telegram. "
                                    + "Then click on the provided linking URL.");
                }
            } else if (messageText.startsWith("/link ")) {
                String receivedToken = messageText.substring("/link ".length()).trim();
                if (receivedToken.isEmpty()) {
                    sendMessage(Long.valueOf(chatId),
                            "Please write a token after /link. You can get it on "
                                    + "our website. Example: `/link abcdef12345`");
                    return;
                }
                processLinkingCommand(receivedToken, telegramUserId, chatId);
            } else {
                sendMessage(Long.valueOf(chatId),
                        "I don`t understand, please try `/start` or `/link <token>`.");
            }
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    private void processLinkingCommand(String receivedToken,
                                       String telegramUserId, String telegramChatId) {
        try {
            telegramAccountLinkingService.processIncomingLinkingToken(
                    receivedToken, telegramUserId, telegramChatId);
            sendMessage(Long.valueOf(telegramChatId),
                    "An unexpected internal error occurred. Please try again later.");

        } catch (InvalidTelegramTokenException e) {
            sendMessage(Long.valueOf(telegramChatId), e.getMessage());
        } catch (LinkingCompletedException e) {
            sendMessage(Long.valueOf(telegramChatId), e.getMessage());
        } catch (RuntimeException e) {
            sendMessage(Long.valueOf(telegramChatId), e.getMessage());
        } catch (Exception e) {
            sendMessage(Long.valueOf(telegramChatId),
                    "An unexpected error occurred during linking. "
                            + "Please try again later or contact support.");
        }
    }

    @PostConstruct
    public void registerBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            System.err.println("ERROR: Failed to register Telegram bot: " + e.getMessage());
        }
    }

    @Override
    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("ERROR: Failed to send a message: " + e.getMessage());
        }
    }
}
