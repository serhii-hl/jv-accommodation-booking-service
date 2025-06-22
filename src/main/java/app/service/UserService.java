package app.service;

import app.dto.CreateUserOwnerRequestDto;
import app.dto.CreateUserRequestDto;
import app.dto.UserDto;

public interface UserService {
    UserDto registerUser(CreateUserRequestDto createUserRequestDto);

    UserDto registerOwnerUser(CreateUserOwnerRequestDto userOwnerRequestDto);
}
