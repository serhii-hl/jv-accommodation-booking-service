package app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import app.model.Accommodation;
import app.util.AccommodationUtils;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccommodationRepositoryTests {
    @Autowired
    private AccommodationRepository accommodationRepository;

    @Test
    @DisplayName("Get owner id by accommodation id ")
    @Sql(scripts = "classpath:database/accommodation/add-accommodation-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/accommodation/remove-accommodation-from-test-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getOwnerIdByAccommodationIdTest() {
        Long id = accommodationRepository.getOwnerIdByAccommodationId(100L);
        assertThat(id.equals(100L)).isTrue();
    }

    @Test
    @DisplayName("Get optional accommodation by id ")
    @Sql(scripts = "classpath:database/accommodation/add-accommodation-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/accommodation/remove-accommodation-from-test-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getOptionalAccommodationByIdTest() {
        Optional<Accommodation> result = accommodationRepository.findById(100L);
        Accommodation accommodation = AccommodationUtils.createExpectedAccommodation();
        Optional<Accommodation> expected = Optional.of(accommodation);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Get all accommodations ")
    @Sql(scripts = "classpath:database/accommodation/add-accommodation-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/accommodation/remove-accommodation-from-test-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllAccommodationTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Accommodation expectedAccommodation = AccommodationUtils.createExpectedAccommodation();
        Page<Accommodation> expectedPage = new PageImpl<>(
                Collections.singletonList(expectedAccommodation),
                pageable, 1
        );

        Page<Accommodation> resultPage = accommodationRepository.findAll(pageable);

        assertThat(resultPage).isEqualTo(expectedPage);
    }

}
