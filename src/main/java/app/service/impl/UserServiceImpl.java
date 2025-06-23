package app.service.impl;

import app.dto.user.CreateUserOwnerRequestDto;
import app.dto.user.CreateUserRequestDto;
import app.dto.user.ProfileDto;
import app.dto.user.UpdateProfileInformationDto;
import app.dto.user.UpdateRoleRequestDto;
import app.dto.user.UserDto;
import app.exception.RegistrationException;
import app.mapper.UserMapper;
import app.model.Role;
import app.model.User;
import app.repository.UserRepository;
import app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto registerUser(CreateUserRequestDto createUserRequestDto) {
        if (userRepository.existsByEmail(createUserRequestDto.getEmail())) {
            throw new RegistrationException("User with such an email already exists: "
                    + createUserRequestDto.getEmail());
        }
        User user = userMapper.toUser(createUserRequestDto);
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(createUserRequestDto.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto registerOwnerUser(CreateUserOwnerRequestDto userOwnerRequestDto) {

        if (userRepository.existsByEmail(userOwnerRequestDto.getEmail())) {
            throw new RegistrationException("User with such an email already exists: "
                    + userOwnerRequestDto.getEmail());
        }
        User user = userMapper.toUser(userOwnerRequestDto);
        user.setRole(Role.OWNER);
        user.setPassword(passwordEncoder.encode(userOwnerRequestDto.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public ProfileDto getCurrentUser(User user) {
        return switch (user.getRole()) {
            case USER -> userMapper.toUserProfileDto(user);
            case OWNER, ADMIN -> userMapper.toOwnerProfileDto(user);
            default -> throw new IllegalStateException("Unexpected value: " + user.getRole());
        };
    }

    @Override
    public void updateUserStatus(Long id, UpdateRoleRequestDto request) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t find user with id: " + id));
        user.setRole(request.getRole());
        user.setDeleted(request.isDeleted());
        userRepository.save(user);
    }

    @Override
    public UserDto updateProfile(User user, UpdateProfileInformationDto dto) {
        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }

        return userMapper.toDto(userRepository.save(user));
    }
}
