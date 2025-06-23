package app.controller;

import app.dto.user.ProfileDto;
import app.dto.user.UpdateProfileInformationDto;
import app.dto.user.UpdateRoleRequestDto;
import app.dto.user.UserDto;
import app.mapper.UserMapper;
import app.model.User;
import app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User controller", description = "Endpoints for user management ( CRUD operations )")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get current user")
    public ProfileDto getCurrentUser(@AuthenticationPrincipal User user) {
        return userService.getCurrentUser(user);
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "Update user by id", description = "Update user by id ( admin only )")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public void updateUserStatus(@PathVariable Long id,
                                 @RequestBody @Valid UpdateRoleRequestDto request) {
        userService.updateUserStatus(id, request);
    }

    @PatchMapping("/me")
    @Operation(summary = "Update current user", description = "Update current user")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateProfile(@AuthenticationPrincipal User user,
                                 @RequestBody @Valid UpdateProfileInformationDto dto) {
        return userService.updateProfile(user, dto);
    }
}
