package app.mapper.impl;

import app.dto.payment.PaymentDto;
import app.mapper.PaymentMapper;
import app.model.Booking;
import app.model.Payment;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-15T17:01:51+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class PaymentMapperImpl implements PaymentMapper {

    @Override
    public PaymentDto toDto(Payment payment) {
        if ( payment == null ) {
            return null;
        }

        PaymentDto paymentDto = new PaymentDto();

        Long id = paymentBookingId( payment );
        if ( id != null ) {
            paymentDto.setBookingId( id );
        }
        if ( payment.getSessionId() != null ) {
            paymentDto.setSessionId( payment.getSessionId() );
        }
        if ( payment.getSessionUrl() != null ) {
            paymentDto.setSessionUrl( payment.getSessionUrl() );
        }
        if ( payment.getId() != null ) {
            paymentDto.setId( payment.getId() );
        }
        if ( payment.getPaymentStatus() != null ) {
            paymentDto.setPaymentStatus( payment.getPaymentStatus() );
        }
        if ( payment.getPrice() != null ) {
            paymentDto.setPrice( payment.getPrice() );
        }
        if ( payment.getCurrency() != null ) {
            paymentDto.setCurrency( payment.getCurrency() );
        }

        return paymentDto;
    }

    private Long paymentBookingId(Payment payment) {
        if ( payment == null ) {
            return null;
        }
        Booking booking = payment.getBooking();
        if ( booking == null ) {
            return null;
        }
        Long id = booking.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
