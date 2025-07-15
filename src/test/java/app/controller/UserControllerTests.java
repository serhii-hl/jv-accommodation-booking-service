package app.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.dto.user.OwnerProfileDto;
import app.dto.user.UpdateProfileInformationDto;
import app.dto.user.UpdateRoleRequestDto;
import app.dto.user.UserDto;
import app.dto.user.UserProfileDto;
import app.model.Role;
import app.model.User;
import app.util.UserUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Successfully get current user with OWNER role")
    @WithUserDetails("owner@example.com")
    @Sql(scripts = "classpath:database/user/add-user-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/user/remove-user-from-test-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getCurrentUser_OwnerRole_Success() throws Exception {
        User expectedUser = UserUtils.createExpectedTestUserOwner();

        MvcResult result = mockMvc.perform(get("/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        OwnerProfileDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), OwnerProfileDto.class);

        assertThat(actualDto).isNotNull();
        assertThat(actualDto.getEmail()).isEqualTo(expectedUser.getEmail());
        assertThat(actualDto.getFirstName()).isEqualTo(expectedUser.getFirstName());
        assertThat(actualDto.getLastName()).isEqualTo(expectedUser.getLastName());
        assertThat(actualDto.getPhoneNumber()).isEqualTo(expectedUser.getPhoneNumber());
        assertThat(actualDto.getCompanyName()).isEqualTo(expectedUser.getCompanyName());
        assertThat(actualDto.getTaxNumber()).isEqualTo(expectedUser.getTaxNumber());
    }

    @Test
    @DisplayName("Successfully get current user with CUSTOMER role")
    @WithUserDetails("customer@example.com")
    @Sql(scripts = "classpath:database/user/add-customer-user-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/user/remove-customer-user-from-test-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getCurrentUser_CustomerRole_Success() throws Exception {
        User expectedUser = UserUtils.createCustomerUser(200L,
                "customer@example.com", "Customer",
                "User", "+1987654321", null);

        MvcResult result = mockMvc.perform(get("/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        UserProfileDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserProfileDto.class);

        assertThat(actualDto).isNotNull();
        assertThat(actualDto.getEmail()).isEqualTo(expectedUser.getEmail());
        assertThat(actualDto.getFirstName()).isEqualTo(expectedUser.getFirstName());
        assertThat(actualDto.getLastName()).isEqualTo(expectedUser.getLastName());
        assertThat(actualDto.getPhoneNumber()).isEqualTo(expectedUser.getPhoneNumber());
    }

    @Test
    @DisplayName("Successfully update user role by ID as ADMIN")
    @WithUserDetails("admin@example.com")
    @Sql(scripts = {
            "classpath:database/user/add-admin-user-to-test-db.sql",
            "classpath:database/user/add-customer-user-to-test-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/user/remove-customer-user-from-test-db.sql",
            "classpath:database/user/remove-admin-user-from-test-db.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateUserStatus_AdminRole_Success() throws Exception {
        Long userIdToUpdate = 200L;
        UpdateRoleRequestDto requestDto = UserUtils.createUpdateRoleRequestDto(
                Role.OWNER, false);

        mockMvc.perform(put("/users/{id}/role", userIdToUpdate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Fail to update user role by ID as non-ADMIN (Forbidden 403)")
    @WithUserDetails("customer@example.com")
    @Sql(scripts = {
            "classpath:database/user/add-customer-user-to-test-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/user/remove-customer-user-from-test-db.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateUserStatus_NonAdminRole_Forbidden() throws Exception {
        Long userIdToUpdate = 200L;
        UpdateRoleRequestDto requestDto = UserUtils.createUpdateRoleRequestDto(
                Role.ADMIN, false);

        mockMvc.perform(put("/users/{id}/role", userIdToUpdate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Fail to update user role by non-existent ID (Not Found 404)")
    @WithUserDetails("admin@example.com")
    @Sql(scripts = "classpath:database/user/add-admin-user-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/user/remove-admin-user-from-test-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateUserStatus_NotFound() throws Exception {
        Long nonExistentUserId = 99999L;
        UpdateRoleRequestDto requestDto = UserUtils.createUpdateRoleRequestDto(
                Role.OWNER, false);

        mockMvc.perform(put("/users/{id}/role", nonExistentUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Can`t find user with id: " + nonExistentUserId)));
    }

    @Test
    @DisplayName("Successfully update current user's profile (all fields)")
    @WithUserDetails("owner@example.com")
    @Sql(scripts = "classpath:database/user/add-user-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/user/remove-user-from-test-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateProfile_AllFields_Success() throws Exception {
        UpdateProfileInformationDto dto = UserUtils.createUpdateProfileInformationDto(
                "NewPassword12563!", "UpdatedFirst",
                "UpdatedLast", "+123456789");

        MvcResult result = mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        UserDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserDto.class);

        assertThat(actualDto).isNotNull();
        assertThat(actualDto.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(actualDto.getLastName()).isEqualTo(dto.getLastName());
        assertThat(actualDto.getPhoneNumber()).isEqualTo(dto.getPhoneNumber());
        assertThat(actualDto.getEmail()).isEqualTo("owner@example.com");
    }

    @Test
    @DisplayName("Successfully update current user's profile (partial update - only first name)")
    @WithUserDetails("owner@example.com")
    @Sql(scripts = "classpath:database/user/add-user-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/user/remove-user-from-test-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateProfile_PartialFields_Success() throws Exception {
        UpdateProfileInformationDto dto = UserUtils.createUpdateProfileInformationDto(
                null, "OnlyFirstNameUpdated", null, null);

        MvcResult result = mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        UserDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserDto.class);

        assertThat(actualDto).isNotNull();
        assertThat(actualDto.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(actualDto.getLastName()).isEqualTo("Owner");
        assertThat(actualDto.getPhoneNumber()).isEqualTo("+1234567890");
        assertThat(actualDto.getEmail()).isEqualTo("owner@example.com");
    }
}
