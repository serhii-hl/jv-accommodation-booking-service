package app.mapper;

import app.config.MapperConfig;
import app.dto.payment.PaymentDto;
import app.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapperConfig.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {
    @Mapping(source = "booking.id", target = "bookingId")
    @Mapping(source = "sessionId", target = "sessionId")
    @Mapping(source = "sessionUrl", target = "sessionUrl")
    PaymentDto toDto(Payment payment);
}
