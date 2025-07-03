package app.controller;

import app.model.User;
import app.service.TelegramAccountLinkingService;
import app.service.TelegramBotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Telegram controller", description = "Endpoints for tg bot management")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class TelegramController {
    private final TelegramAccountLinkingService telegramAccountLinkingService;
    private final TelegramBotService telegramBotService;

    @PostMapping("/generate-linking-url")
    @Operation(summary = "Generate a link to access bot",
            description = "Generate a link with token to link tg user with user in our system")
    public ResponseEntity<String> generateTelegramLinkingUrl(
            @AuthenticationPrincipal User currentUser) {
        try {
            String token = telegramAccountLinkingService.generateLinkingTokenForUser(currentUser);
            String botUsername = telegramBotService.getBotUsername();
            String telegramDeepLink = String.format("https://t.me/%s?start=%s", botUsername, token);
            return ResponseEntity.ok(telegramDeepLink);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Failed to generate linking URL. "
                            + "Please try again later or contact support.");
        }
    }
}
