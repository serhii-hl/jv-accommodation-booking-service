package app.service;

import app.dto.user.CreateUserOwnerRequestDto;
import app.dto.user.CreateUserRequestDto;
import app.dto.user.ProfileDto;
import app.dto.user.UpdateProfileInformationDto;
import app.dto.user.UpdateRoleRequestDto;
import app.dto.user.UserDto;
import app.model.User;

public interface UserService {
    UserDto registerUser(CreateUserRequestDto createUserRequestDto);

    UserDto registerOwnerUser(CreateUserOwnerRequestDto userOwnerRequestDto);

    ProfileDto getCurrentUser(User user);

    void updateUserStatus(Long id, UpdateRoleRequestDto request);

    UserDto updateProfile(User user, UpdateProfileInformationDto dto);
}
