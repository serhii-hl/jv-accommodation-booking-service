package app.util;

import app.dto.user.CreateUserOwnerRequestDto;
import app.dto.user.CreateUserRequestDto;
import app.dto.user.UpdateProfileInformationDto;
import app.dto.user.UpdateRoleRequestDto;
import app.dto.user.UserDto;
import app.dto.user.UserLoginRequestDto;
import app.dto.user.UserLoginResponseDto;
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

    public static CreateUserRequestDto createCreateUserRequestDto(
            String email, String firstName, String lastName,
            String password, String confirmPassword) {
        CreateUserRequestDto dto = new CreateUserRequestDto();
        dto.setEmail(email);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setPassword(password);
        dto.setConfirmPassword(confirmPassword);
        return dto;
    }

    public static CreateUserOwnerRequestDto createCreateUserOwnerRequestDto(
            String email, String firstName, String lastName,
            String password, String confirmPassword,
            String phoneNumber, String taxNumber, String companyName) {
        CreateUserOwnerRequestDto dto = new CreateUserOwnerRequestDto();
        dto.setEmail(email);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setPassword(password);
        dto.setConfirmPassword(confirmPassword);
        dto.setPhoneNumber(phoneNumber);
        dto.setTaxNumber(taxNumber);
        dto.setCompanyName(companyName);
        return dto;
    }

    public static UserDto createUserDto(
            Long id, String email, String firstName, String lastName, String phoneNumber) {
        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setEmail(email);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setPhoneNumber(phoneNumber);
        return dto;
    }

    public static UserLoginRequestDto createUserLoginRequestDto(String email, String password) {
        UserLoginRequestDto dto = new UserLoginRequestDto();
        dto.setEmail(email);
        dto.setPassword(password);
        return dto;
    }

    public static UserLoginResponseDto createUserLoginResponseDto(String jwtToken) {
        UserLoginResponseDto dto = new UserLoginResponseDto();
        dto.setJwtToken(jwtToken);
        return dto;
    }

    public static UpdateRoleRequestDto createUpdateRoleRequestDto(Role role, boolean isDeleted) {
        UpdateRoleRequestDto dto = new UpdateRoleRequestDto();
        dto.setRole(role);
        dto.setDeleted(isDeleted);
        return dto;
    }

    public static UpdateProfileInformationDto createUpdateProfileInformationDto(
            String password, String firstName, String lastName, String phoneNumber) {
        UpdateProfileInformationDto updateProfileInformationDto = new UpdateProfileInformationDto();
        updateProfileInformationDto.setPassword(password);
        updateProfileInformationDto.setFirstName(firstName);
        updateProfileInformationDto.setLastName(lastName);
        updateProfileInformationDto.setPhoneNumber(phoneNumber);
        return updateProfileInformationDto;
    }
}
