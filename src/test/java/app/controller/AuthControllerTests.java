package app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.dto.user.CreateUserOwnerRequestDto;
import app.dto.user.CreateUserRequestDto;
import app.dto.user.UserDto;
import app.dto.user.UserLoginRequestDto;
import app.dto.user.UserLoginResponseDto;
import app.security.AuthenticationService;
import app.service.UserService;
import app.util.UserUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    @DisplayName("Register a new user successfully and return 201 Created")
    void registerUser_Success() throws Exception {
        CreateUserRequestDto requestDto = UserUtils.createCreateUserRequestDto(
                "user@example.com", "First",
                "User", "UserPassword123!", "UserPassword123!");
        UserDto expectedUserDto = UserUtils.createUserDto(
                1L, "user@example.com", "First",
                "User", "+1112223333");
        when(userService.registerUser(any(CreateUserRequestDto.class))).thenReturn(expectedUserDto);
        mockMvc.perform(post("/auth/register/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedUserDto.getId()))
                .andExpect(jsonPath("$.email").value(expectedUserDto.getEmail()))
                .andExpect(jsonPath("$.firstName").value(expectedUserDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(expectedUserDto.getLastName()))
                .andExpect(jsonPath("$.phoneNumber")
                        .value(expectedUserDto.getPhoneNumber()));
    }

    @Test
    @DisplayName("Register a new owner successfully and return 201 Created")
    void registerOwner_Success() throws Exception {
        CreateUserOwnerRequestDto requestDto = UserUtils.createCreateUserOwnerRequestDto(
                "owner2@example.com", "Second", "Owner",
                "OwnerPassword123!", "OwnerPassword123!",
                "+123456789", "1234567890",
                "My Awesome Accommodations Inc.");
        UserDto expectedUserDto = UserUtils.createUserDto(
                2L, requestDto.getEmail(), requestDto.getFirstName(),
                requestDto.getLastName(), requestDto.getPhoneNumber());
        when(userService.registerOwnerUser(any(CreateUserOwnerRequestDto.class)))
                .thenReturn(expectedUserDto);
        mockMvc.perform(post("/auth/register/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedUserDto.getId()))
                .andExpect(jsonPath("$.email").value(expectedUserDto.getEmail()))
                .andExpect(jsonPath("$.firstName").value(expectedUserDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(expectedUserDto.getLastName()))
                .andExpect(jsonPath("$.phoneNumber")
                        .value(expectedUserDto.getPhoneNumber()));
    }

    @Test
    @DisplayName("Login user successfully and return 200 OK")
    void login_Success() throws Exception {
        UserLoginRequestDto requestDto = UserUtils
                .createUserLoginRequestDto("user@example.com", "Password123!");
        UserLoginResponseDto expectedResponseDto =
                UserUtils.createUserLoginResponseDto("mockedJwtToken");
        when(authenticationService.authenticate(any(UserLoginRequestDto.class)))
                .thenReturn(expectedResponseDto);
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken")
                        .value(expectedResponseDto.getJwtToken()));
    }
}
