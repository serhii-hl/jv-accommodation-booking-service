package app.mapper;

import app.config.MapperConfig;
import app.dto.user.CreateUserOwnerRequestDto;
import app.dto.user.CreateUserRequestDto;
import app.dto.user.OwnerProfileDto;
import app.dto.user.UserDto;
import app.dto.user.UserProfileDto;
import app.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapperConfig.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto toDto(User user);

    User toUser(CreateUserRequestDto createUserRequestDto);

    User toUser(CreateUserOwnerRequestDto userOwnerRequestDto);

    UserProfileDto toUserProfileDto(User user);

    OwnerProfileDto toOwnerProfileDto(User user);
}
