package app.mapper;

import app.config.MapperConfig;
import app.dto.CreateUserOwnerRequestDto;
import app.dto.CreateUserRequestDto;
import app.dto.UserDto;
import app.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapperConfig.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    public UserDto toDto(User user);

    public User toUser(CreateUserRequestDto createUserRequestDto);

    public User toUser(CreateUserOwnerRequestDto userOwnerRequestDto);
}
