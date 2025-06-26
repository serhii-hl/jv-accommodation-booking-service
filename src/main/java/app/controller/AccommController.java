package app.controller;

import app.dto.accommodation.AccommodationDto;
import app.dto.accommodation.CreateAccommodationDto;
import app.dto.accommodation.UpdateAccommodationDto;
import app.exception.RegistrationException;
import app.model.User;
import app.service.AccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Accommodation controller",
        description = "Endpoints for accommodation management ( CRUD operations )")
@RestController
@RequestMapping("/accommodations")
@RequiredArgsConstructor
public class AccommController {
    private final AccommodationService accommodationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') || hasRole('OWNER')")
    @Operation(summary = "creates accommodation", description = "Creates a new accommodation")
    public AccommodationDto createAccommodation(@AuthenticationPrincipal User user,
                                                @RequestBody @Valid CreateAccommodationDto dto)
            throws RegistrationException {
        return accommodationService.createAccommodation(dto, user);
    }

    @GetMapping
    @Operation(summary = "get all accommodations", description = "Get all accommodations")
    public Page<AccommodationDto> getAllAccommodations(Pageable pageable) {
        return accommodationService.getAllAccommodations(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "get accommodation", description = "Get accommodation by id")
    public AccommodationDto getAccommodationById(@PathVariable Long id) {
        return accommodationService.getAccommodationById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') || hasRole('OWNER')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "delete accommodation", description = "Delete accommodation by id")
    public void deleteAccommodationById(@PathVariable Long id) {
        accommodationService.deleteAccommodationById(id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') || hasRole('OWNER')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "update accommodation", description = "Update accommodation by id")
    public AccommodationDto updateAccommodationById(
            @PathVariable Long id, @RequestBody @Valid UpdateAccommodationDto dto) {
        return accommodationService.updateAccommodationById(id, dto);
    }
}
