package app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.model.User;
import app.service.TelegramAccountLinkingService;
import app.service.TelegramBotService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "classpath:database/user/add-user-to-test-db.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/user/remove-user-from-test-db.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TelegramControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TelegramAccountLinkingService telegramAccountLinkingService;

    @MockBean
    private TelegramBotService telegramBotService;

    @Test
    @DisplayName("Should return deep link for Telegram bot when called by authenticated user")
    @WithUserDetails("owner@example.com")
    void generateTelegramLinkingUrl_Success() throws Exception {
        when(telegramAccountLinkingService.generateLinkingTokenForUser(any(User.class)))
                .thenReturn("mocked-token-123");
        when(telegramBotService.getBotUsername())
                .thenReturn("MyAwesomeBot");

        mockMvc.perform(post("/users/generate-linking-url"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "https://t.me/MyAwesomeBot?start=mocked-token-123"));
    }

    @Test
    @DisplayName("Should return 500 when linking URL generation fails")
    @WithUserDetails("owner@example.com")
    void generateTelegramLinkingUrl_Exception_ReturnsInternalServerError() throws Exception {
        when(telegramAccountLinkingService.generateLinkingTokenForUser(any(User.class)))
                .thenThrow(new RuntimeException("Database unavailable"));

        mockMvc.perform(post("/users/generate-linking-url"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to generate linking URL. "
                        + "Please try again later or contact support."));
    }
}
