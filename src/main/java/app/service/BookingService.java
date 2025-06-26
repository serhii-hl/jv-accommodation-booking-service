package app.service;

import app.dto.booking.BookingDto;
import app.dto.booking.CreateBookingDto;
import app.model.BookingStatus;
import app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingDto getBookingById(Long id);

    Page<BookingDto> getAllUserBookings(User user, Pageable pageable);

    Page<BookingDto> getFilteredBookingsForAdmin(Long userId,
                                                 BookingStatus status, Pageable pageable);

    Page<BookingDto> getFilteredBookingsForOwner(
            Long userId, BookingStatus status, Pageable pageable, User owner);

    void deleteBookingById(Long id);

    BookingDto updateBookingStatus(Long id, BookingStatus status);

    BookingDto createBooking(CreateBookingDto createBookingDto, User user);
}
