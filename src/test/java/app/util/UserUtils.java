package app.util;

import app.dto.user.CreateUserRequestDto;
import app.dto.user.UserDto;
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

    public static User createCustomerUser(Long id, String email,
                                          String firstName, String lastName,
                                          String phoneNumber, String telegramUserId) {
        User user = new User();
        user.setId(id);
        user.setCompanyName(null);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setDeleted(false);
        user.setLastName(lastName);
        user.setPassword("customerPass");
        user.setPhoneNumber(phoneNumber);
        user.setRole(Role.USER);
        user.setTaxNumber(null);
        user.setTelegramChatId(null);
        user.setTelegramUserId(telegramUserId);
        return user;
    }

    public static CreateUserRequestDto createCreateUserRequestDtoFromOwner() {
        User owner = createExpectedTestUserOwner();
        CreateUserRequestDto dto = new CreateUserRequestDto();
        dto.setEmail(owner.getEmail());
        dto.setFirstName(owner.getFirstName());
        dto.setLastName(owner.getLastName());
        dto.setPassword(owner.getPassword());
        dto.setConfirmPassword(owner.getPassword());
        return dto;
    }

    public static UserDto createUserDtoFromOwner() {
        User owner = createExpectedTestUserOwner();
        UserDto dto = new UserDto();
        dto.setId(owner.getId());
        dto.setEmail(owner.getEmail());
        dto.setFirstName(owner.getFirstName());
        dto.setLastName(owner.getLastName());
        dto.setPhoneNumber(owner.getPhoneNumber());
        return dto;
    }
}
