package app.service.impl;

import app.dto.CreateUserOwnerRequestDto;
import app.dto.CreateUserRequestDto;
import app.dto.UserDto;
import app.exception.RegistrationException;
import app.mapper.UserMapper;
import app.model.Role;
import app.model.User;
import app.repository.UserRepository;
import app.service.UserService;
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
}
