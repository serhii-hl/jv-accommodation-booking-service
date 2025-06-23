package app.controller;

import app.dto.user.CreateUserOwnerRequestDto;
import app.dto.user.CreateUserRequestDto;
import app.dto.user.UserDto;
import app.dto.user.UserLoginRequestDto;
import app.dto.user.UserLoginResponseDto;
import app.exception.RegistrationException;
import app.security.AuthenticationService;
import app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth controller", description = "Endpoints for authentication and registration")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/register/user")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register user", description = "Register a new user")
    public UserDto registerUser(@RequestBody @Valid CreateUserRequestDto request)
            throws RegistrationException {
        return userService.registerUser(request);
    }

    @PostMapping("/register/owner")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register owner", description = "Register a new owner")
    public UserDto registerOwner(@RequestBody @Valid CreateUserOwnerRequestDto request)
            throws RegistrationException {
        return userService.registerOwnerUser(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Login user")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }
}
