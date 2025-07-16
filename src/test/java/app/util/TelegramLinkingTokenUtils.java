package app.util;

import app.model.Role;
import app.model.TelegramLinkingToken;
import app.model.User;
import java.time.LocalDateTime;

public class TelegramLinkingTokenUtils {

    private static User createTestUserForToken() {
        User user = new User();
        user.setId(100L);
        user.setCompanyName("Test Company");
        user.setEmail("owner@example.com");
        user.setFirstName("Test");
        user.setDeleted(false);
        user.setLastName("Owner");
        user.setPassword("Password1234!");
        user.setPhoneNumber("+1234567890");
        user.setRole(Role.OWNER);
        user.setTaxNumber("1234567890");
        user.setTelegramChatId("123456789");
        user.setTelegramUserId("987654321");
        return user;
    }

    public static TelegramLinkingToken createExpectedTelegramLinkingToken() {
        User user = createTestUserForToken();
        TelegramLinkingToken token = new TelegramLinkingToken();
        token.setId(100L);
        token.setToken("test token");
        token.setUser(user);
        token.setCreatedAt(LocalDateTime.of(2025, 7, 5, 0, 0, 0));
        token.setExpiresAt(LocalDateTime.of(2025, 7, 7, 0, 0, 0));
        token.setUsed(false);
        return token;
    }
}
