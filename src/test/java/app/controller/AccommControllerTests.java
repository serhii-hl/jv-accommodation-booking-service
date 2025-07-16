package app.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.dto.accommodation.AccommodationDto;
import app.dto.accommodation.CreateAccommodationDto;
import app.dto.accommodation.UpdateAccommodationDto;
import app.dto.location.LocationDto;
import app.util.AccommodationUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AccommControllerTests {

    private static final Pageable PAGEABLE = PageRequest.of(0, 10);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(@Autowired WebApplicationContext context) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Successfully creates an accommodation")
    @WithUserDetails("owner@example.com")
    @Sql(scripts = "classpath:database/user/add-user-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/user/remove-user-from-test-db-for-controller.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createAccommodation_ValidDto_ReturnsCreated() throws Exception {
        CreateAccommodationDto dto = AccommodationUtils.createExpectedCreateAccommodationDto();

        MvcResult result = mockMvc.perform(post("/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        AccommodationDto actualResponseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationDto.class);

        assertThat(actualResponseDto).isNotNull();
        assertThat(actualResponseDto.getId()).isNotNull();
        AccommodationDto expectedResponseDto = AccommodationUtils.createExpectedAccommodationDto();

        assertThat(actualResponseDto)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedResponseDto);
    }

    @Test
    @DisplayName("Successfully return all accommodations")
    @WithUserDetails("owner@example.com")
    @Sql(scripts = "classpath:database/accommodation/add-accommodation-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/accommodation/remove-accommodation-from-test-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAllAccommodations() throws Exception {
        AccommodationDto expectedSingleAccommodationDto =
                AccommodationUtils.createExpectedAccommodationDto();
        List<AccommodationDto> expectedContent = List.of(expectedSingleAccommodationDto);
        MvcResult result = mockMvc.perform(get("/accommodations")
                        .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                        .param("size", String.valueOf(PAGEABLE.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        List<AccommodationDto> actualContent = objectMapper.convertValue(
                rootNode.get("content"),
                new TypeReference<List<AccommodationDto>>() {}
        );
        long actualTotalElements = rootNode.get("totalElements").asLong();
        assertThat(actualContent).isNotNull().hasSize(expectedContent.size());
        assertThat(actualTotalElements).isEqualTo(expectedContent.size());
        assertThat(actualContent)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .isEqualTo(expectedContent);
        assertThat(actualContent.get(0).getId()).isEqualTo(expectedSingleAccommodationDto.getId());
    }

    @Test
    @DisplayName("Successfully return accommodation by ID")
    @WithUserDetails("owner@example.com")
    @Sql(scripts = "classpath:database/accommodation/add-accommodation-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/accommodation/remove-accommodation-from-test-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAccommodationById_Success() throws Exception {
        Long accommodationId = 100L;
        AccommodationDto expectedDto = AccommodationUtils.createExpectedAccommodationDto();

        MvcResult result = mockMvc.perform(get("/accommodations/{id}", accommodationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        AccommodationDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationDto.class);

        assertThat(actualDto).isNotNull();
        assertThat(actualDto.getId()).isEqualTo(accommodationId);
        assertThat(actualDto)
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("Get accommodation by non-existent ID fails with 404 Not Found")
    @WithUserDetails("owner@example.com")
    @Sql(scripts = "classpath:database/user/add-user-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/user/remove-user-from-test-db-for-controller.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAccommodationById_NotFound() throws Exception {
        Long nonExistentAccommodationId = 999L;

        mockMvc.perform(get("/accommodations/{id}", nonExistentAccommodationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(
                        "Can`t find accommodation by id " + nonExistentAccommodationId)));
    }

    @Test
    @DisplayName("Successfully delete accommodation by ID (owner)")
    @WithUserDetails("owner@example.com")
    @Sql(scripts = "classpath:database/accommodation/add-accommodation-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/accommodation/remove-accommodation-from-test-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteAccommodationById_Success_Owner() throws Exception {
        Long accommodationId = 100L;

        mockMvc.perform(delete("/accommodations/{id}", accommodationId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/accommodations/{id}", accommodationId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete accommodation by non-existent ID fails with 404 status")
    @WithUserDetails("owner@example.com")
    @Sql(scripts = "classpath:database/user/add-user-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/user/remove-user-from-test-db-for-controller.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteAccommodationById_NotFound() throws Exception {
        Long nonExistentAccommodationId = 999L;

        mockMvc.perform(delete("/accommodations/{id}", nonExistentAccommodationId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(
                        "Accommodation with id: " + nonExistentAccommodationId + " not found.")));
    }

    @Test
    @DisplayName("Delete accommodation by ID fails for unauthorized user (customer)")
    @WithUserDetails("customer@example.com")
    @Sql(scripts = {
            "classpath:database/user/add-customer-user-to-test-db.sql",
            "classpath:database/accommodation/add-accommodation-to-test-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/accommodation/remove-accommodation-from-test-db.sql",
            "classpath:database/user/remove-user-from-test-db-for-controller.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteAccommodationById_Unauthorized() throws Exception {
        Long accommodationId = 100L;

        mockMvc.perform(delete("/accommodations/{id}", accommodationId))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Successfully update accommodation by ID (owner)")
    @WithUserDetails("owner@example.com")
    @Sql(scripts = "classpath:database/accommodation/add-accommodation-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/accommodation/remove-accommodation-from-test-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateAccommodationById_Success_Owner() throws Exception {
        BigDecimal newDailyPrice = new BigDecimal("99.99");
        UpdateAccommodationDto updateDto =
                AccommodationUtils.createExpectedUpdateAccommodationDto();
        updateDto.setDailyPrice(newDailyPrice);
        String newStreet = "New Test Street";
        LocationDto updatedLocationDto = AccommodationUtils.createExpectedLocationDto();
        updatedLocationDto.setStreet(newStreet);
        updateDto.setLocation(updatedLocationDto);

        AccommodationDto expectedDto = AccommodationUtils.createExpectedAccommodationDto();
        expectedDto.setDailyPrice(newDailyPrice);
        expectedDto.getLocation().setStreet(newStreet);
        Long accommodationId = 100L;

        MvcResult result = mockMvc.perform(patch("/accommodations/{id}", accommodationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        AccommodationDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationDto.class);

        assertThat(actualDto).isNotNull();
        assertThat(actualDto.getId()).isEqualTo(accommodationId);
        assertThat(actualDto)
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("Update accommodation by non-existent ID fails with 404 Not Found")
    @WithUserDetails("owner@example.com")
    @Sql(scripts = "classpath:database/user/add-user-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/user/remove-user-from-test-db-for-controller.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateAccommodationById_NotFound() throws Exception {
        Long nonExistentAccommodationId = 999L;
        UpdateAccommodationDto updateDto = new UpdateAccommodationDto();
        updateDto.setDailyPrice(new BigDecimal("123.45"));

        mockMvc.perform(patch("/accommodations/{id}", nonExistentAccommodationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(
                        "Can`t find accommodation by id " + nonExistentAccommodationId)));
    }

    @Test
    @DisplayName("Update accommodation by ID fails for unauthorized user (customer)")
    @WithUserDetails("customer@example.com")
    @Sql(scripts = {
            "classpath:database/accommodation/add-accommodation-to-test-db.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/accommodation/remove-accommodation-from-test-db.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateAccommodationById_Unauthorized() throws Exception {
        Long accommodationId = 100L;
        UpdateAccommodationDto updateDto = new UpdateAccommodationDto();
        updateDto.setDailyPrice(new BigDecimal("123.45"));

        mockMvc.perform(patch("/accommodations/{id}", accommodationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden());
    }
}
