package app.controller;

import app.dto.booking.BookingDto;
import app.dto.booking.CreateBookingDto;
import app.model.BookingStatus;
import app.model.User;
import app.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Booking controller",
        description = "Endpoints for booking management ( CRUD operations )")
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/my")
    @Operation(summary = "get all user`s bookings", description = "Get all user`s bookings")
    Page<BookingDto> getAllUserBookings(@AuthenticationPrincipal User user, Pageable pageable) {
        return bookingService.getAllUserBookings(user, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "get booking by id", description = "Get booking by id")
    BookingDto getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get filtered bookings for admin",
            description = "Get bookings by optional parameters for ADMINs")
    Page<BookingDto> getFilteredBookingsForAdmin(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) BookingStatus status,
            Pageable pageable) {
        return bookingService.getFilteredBookingsForAdmin(userId, status, pageable);
    }

    @GetMapping("/owner")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Get filtered bookings for owner",
            description = "Get bookings by optional parameters for OWNERs")
    Page<BookingDto> getFilteredBookingsForOwner(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) BookingStatus status,
            Pageable pageable) {
        return bookingService.getFilteredBookingsForOwner(userId, status, pageable, user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete booking by id",
            description = "Delete booking by id (Admin only)")
    void deleteBookingById(@PathVariable Long id) {
        bookingService.deleteBookingById(id);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates booking",
            description = "Creates booking")
    BookingDto createBooking(@RequestBody CreateBookingDto createBookingDto,
                             @AuthenticationPrincipal User user) {
        return bookingService.createBooking(createBookingDto, user);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Updates booking status by id",
            description = "Updates booking status by id (admin only)")
    BookingDto updateBookingStatus(@PathVariable Long id, @RequestBody BookingStatus status) {
        return bookingService.updateBookingStatus(id, status);
    }
}
