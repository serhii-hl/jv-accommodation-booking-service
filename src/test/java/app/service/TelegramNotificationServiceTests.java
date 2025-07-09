package app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import app.service.impl.TelegramNotificationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ExtendWith(MockitoExtension.class)
public class TelegramNotificationServiceTests {

    @InjectMocks
    private TelegramNotificationServiceImpl telegramNotificationService;

    @Mock
    private TelegramBotService telegramBotService;

    @Test
    @DisplayName("Should successfully send notification")
    void sendNotificationSuccess() throws TelegramApiException {
        String chatId = "123456789";
        String message = "Test notification message";

        doNothing().when(telegramBotService).sendMessage(any(Long.class), anyString());

        telegramNotificationService.sendNotification(chatId, message);

        verify(telegramBotService, times(1)).sendMessage(Long.valueOf(chatId), message);
    }

    @Test
    @DisplayName("Should throw RuntimeException when chatId is invalid")
    void sendNotificationFailure_InvalidChatId() {
        String invalidChatId = "abc";
        String message = "Test notification message";

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> telegramNotificationService.sendNotification(invalidChatId, message),
                "Expected RuntimeException for invalid chat ID"
        );
        assertThat(thrown.getMessage()).contains("Cannot send notification");
        assertThat(thrown.getCause()).isInstanceOf(NumberFormatException.class);

        verify(telegramBotService, never()).sendMessage(any(Long.class), anyString());
    }

    @Test
    @DisplayName("Should throw RuntimeException when TelegramBotService throws an exception")
    void sendNotificationFailure_TelegramBotServiceException() {
        String chatId = "123456789";
        String message = "Test notification message";
        TelegramApiException telegramApiException = new TelegramApiException("Telegram API error");
        doThrow(new RuntimeException("Simulated Telegram API error", telegramApiException))
                .when(telegramBotService).sendMessage(any(Long.class), anyString());

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> telegramNotificationService.sendNotification(chatId, message),
                "Expected RuntimeException when TelegramBotService throws an exception"
        );
        assertThat(thrown.getMessage()).contains("Cannot send notification");
        assertThat(thrown.getCause().getCause()).isEqualTo(telegramApiException);

        verify(telegramBotService, times(1)).sendMessage(Long.valueOf(chatId), message);
    }
}
