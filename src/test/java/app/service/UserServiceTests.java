package app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.dto.user.CreateUserRequestDto;
import app.dto.user.OwnerProfileDto;
import app.dto.user.ProfileDto;
import app.dto.user.UpdateProfileInformationDto;
import app.dto.user.UpdateRoleRequestDto;
import app.dto.user.UserDto;
import app.dto.user.UserProfileDto;
import app.exception.RegistrationException;
import app.mapper.UserMapper;
import app.model.Role;
import app.model.User;
import app.repository.UserRepository;
import app.service.impl.UserServiceImpl;
import app.util.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("register user - success")
    void registerUserSuccess() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        CreateUserRequestDto createUserRequestDto = UserUtils
                .createCreateUserRequestDtoFromOwner();
        User expectedUser = UserUtils.createExpectedTestUserOwner();
        when(userMapper.toUser(createUserRequestDto)).thenReturn(expectedUser);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userRepository.save(any())).thenReturn(expectedUser);
        UserDto expectedUserDto = UserUtils.createUserDtoFromOwner();
        when(userMapper.toDto(expectedUser)).thenReturn(expectedUserDto);
        UserDto actual = userService.registerUser(createUserRequestDto);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expectedUserDto);
    }

    @Test
    @DisplayName("register user - failure")
    void registerUserFailure() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        CreateUserRequestDto createUserRequestDto = UserUtils.createCreateUserRequestDtoFromOwner();
        RegistrationException ex = assertThrows(
                RegistrationException.class,
                () -> userService.registerUser(createUserRequestDto)
        );
        assertThat(ex.getMessage()).contains("User with such an email already exists");
        assertThat(ex.getMessage()).contains(createUserRequestDto.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("getCurrentUser - USER returns UserProfileDto")
    void getCurrentUser_UserRole_ReturnsUserProfileDto() {
        User user = UserUtils.createCustomerUser(
                200L, "user@example.com", "John",
                "Doe", "+1234567899", "tg_user_001");
        user.setRole(Role.USER);
        UserProfileDto expectedDto = new UserProfileDto();
        expectedDto.setEmail(user.getEmail());
        expectedDto.setFirstName(user.getFirstName());
        expectedDto.setLastName(user.getLastName());
        when(userMapper.toUserProfileDto(user)).thenReturn(expectedDto);
        ProfileDto result = userService.getCurrentUser(user);
        assertThat(result).isEqualTo(expectedDto);
        verify(userMapper).toUserProfileDto(user);
        verify(userMapper, never()).toOwnerProfileDto(any());
    }

    @Test
    @DisplayName("getCurrentUser - OWNER returns OwnerProfileDto")
    void getCurrentUser_OwnerRole_ReturnsOwnerProfileDto() {
        User owner = UserUtils.createExpectedTestUserOwner();
        owner.setRole(Role.OWNER);
        OwnerProfileDto expectedDto = new OwnerProfileDto();
        expectedDto.setCompanyName(owner.getCompanyName());
        expectedDto.setEmail(owner.getEmail());
        when(userMapper.toOwnerProfileDto(owner)).thenReturn(expectedDto);
        ProfileDto actual = userService.getCurrentUser(owner);
        assertThat(actual).isEqualTo(expectedDto);
        verify(userMapper).toOwnerProfileDto(owner);
        verify(userMapper, never()).toUserProfileDto(any());
    }

    @Test
    @DisplayName("getCurrentUser - unexpected role throws exception")
    void getCurrentUser_InvalidRole_ThrowsException() {
        User user = UserUtils.createExpectedTestUserOwner();
        user.setRole(null);
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> userService.getCurrentUser(user));
        assertThat(ex.getMessage()).contains("User role must not be null");
    }

    @Test
    @DisplayName("updateUserStatus - success")
    void updateUserStatus_Success() {
        Long userId = 100L;
        UpdateRoleRequestDto request = new UpdateRoleRequestDto();
        request.setRole(Role.ADMIN);
        request.setDeleted(true);
        User existingUser = UserUtils.createExpectedTestUserOwner();
        existingUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        userService.updateUserStatus(userId, request);
        assertThat(existingUser.getRole()).isEqualTo(request.getRole());
        assertThat(existingUser.isDeleted()).isEqualTo(request.isDeleted());
        verify(userRepository).findById(userId);
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("updateUserStatus - user not found throws exception")
    void updateUserStatus_UserNotFound_ThrowsException() {
        Long userId = 999L;
        UpdateRoleRequestDto request = new UpdateRoleRequestDto();
        request.setRole(Role.USER);
        request.setDeleted(false);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> userService.updateUserStatus(userId, request));
        assertThat(ex.getMessage()).contains("Can`t find user with id: " + userId);
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateProfile - all fields updated successfully")
    void updateProfile_AllFieldsUpdated() {
        UpdateProfileInformationDto dto = new UpdateProfileInformationDto();
        dto.setPassword("newPassword123");
        dto.setFirstName("NewFirst");
        dto.setLastName("NewLast");
        dto.setPhoneNumber("+111222333");
        String encodedPassword = "encodedPassword123";
        when(passwordEncoder.encode(dto.getPassword())).thenReturn(encodedPassword);
        User savedUser = new User();
        User user = UserUtils.createExpectedTestUserOwner();
        savedUser.setId(user.getId());
        savedUser.setPassword(encodedPassword);
        savedUser.setFirstName(dto.getFirstName());
        savedUser.setLastName(dto.getLastName());
        savedUser.setPhoneNumber(dto.getPhoneNumber());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        UserDto expectedDto = new UserDto();
        expectedDto.setId(savedUser.getId());
        expectedDto.setEmail(user.getEmail());
        expectedDto.setFirstName(savedUser.getFirstName());
        expectedDto.setLastName(savedUser.getLastName());
        expectedDto.setPhoneNumber(savedUser.getPhoneNumber());
        when(userMapper.toDto(savedUser)).thenReturn(expectedDto);
        UserDto actual = userService.updateProfile(user, dto);
        assertThat(user.getPassword()).isEqualTo(encodedPassword);
        assertThat(user.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(user.getLastName()).isEqualTo(dto.getLastName());
        assertThat(user.getPhoneNumber()).isEqualTo(dto.getPhoneNumber());
        assertThat(actual).isEqualTo(expectedDto);
        verify(passwordEncoder).encode(dto.getPassword());
        verify(userRepository).save(user);
        verify(userMapper).toDto(savedUser);
    }

    @Test
    @DisplayName("updateProfile - only some fields updated")
    void updateProfile_SomeFieldsUpdated() {
        UpdateProfileInformationDto dto = new UpdateProfileInformationDto();
        dto.setPassword(null);
        dto.setFirstName("ChangedFirstName");
        dto.setLastName(null);
        dto.setPhoneNumber(null);
        User savedUser = new User();
        User user = UserUtils.createExpectedTestUserOwner();
        savedUser.setId(user.getId());
        savedUser.setPassword(user.getPassword());
        savedUser.setFirstName(dto.getFirstName());
        savedUser.setLastName(user.getLastName());
        savedUser.setPhoneNumber(user.getPhoneNumber());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        UserDto expectedDto = new UserDto();
        expectedDto.setId(savedUser.getId());
        expectedDto.setEmail(user.getEmail());
        expectedDto.setFirstName(savedUser.getFirstName());
        expectedDto.setLastName(savedUser.getLastName());
        expectedDto.setPhoneNumber(savedUser.getPhoneNumber());
        when(userMapper.toDto(savedUser)).thenReturn(expectedDto);
        UserDto actual = userService.updateProfile(user, dto);
        assertThat(user.getPassword()).isNotNull();
        assertThat(user.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(user.getLastName())
                .isEqualTo(UserUtils.createExpectedTestUserOwner().getLastName());
        assertThat(user.getPhoneNumber())
                .isEqualTo(UserUtils.createExpectedTestUserOwner().getPhoneNumber());
        assertThat(actual).isEqualTo(expectedDto);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(user);
        verify(userMapper).toDto(savedUser);
    }
}
