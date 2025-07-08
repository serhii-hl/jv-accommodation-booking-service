package app.util;

import app.model.Role;
import app.model.User;

public class UserUtils {
    public static User createExpectedTestUserOwner() {
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
}
