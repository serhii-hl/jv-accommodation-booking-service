package app.mapper.impl;

import app.dto.CreateUserOwnerRequestDto;
import app.dto.CreateUserRequestDto;
import app.dto.UserDto;
import app.mapper.UserMapper;
import app.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-22T10:37:49+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        return userDto;
    }

    @Override
    public User toUser(CreateUserRequestDto createUserRequestDto) {
        if ( createUserRequestDto == null ) {
            return null;
        }

        User user = new User();

        if ( createUserRequestDto.getEmail() != null ) {
            user.setEmail( createUserRequestDto.getEmail() );
        }
        if ( createUserRequestDto.getFirstName() != null ) {
            user.setFirstName( createUserRequestDto.getFirstName() );
        }
        if ( createUserRequestDto.getLastName() != null ) {
            user.setLastName( createUserRequestDto.getLastName() );
        }
        if ( createUserRequestDto.getPassword() != null ) {
            user.setPassword( createUserRequestDto.getPassword() );
        }

        return user;
    }

    @Override
    public User toUser(CreateUserOwnerRequestDto userOwnerRequestDto) {
        if ( userOwnerRequestDto == null ) {
            return null;
        }

        User user = new User();

        if ( userOwnerRequestDto.getEmail() != null ) {
            user.setEmail( userOwnerRequestDto.getEmail() );
        }
        if ( userOwnerRequestDto.getFirstName() != null ) {
            user.setFirstName( userOwnerRequestDto.getFirstName() );
        }
        if ( userOwnerRequestDto.getLastName() != null ) {
            user.setLastName( userOwnerRequestDto.getLastName() );
        }
        if ( userOwnerRequestDto.getPassword() != null ) {
            user.setPassword( userOwnerRequestDto.getPassword() );
        }
        if ( userOwnerRequestDto.getTaxNumber() != null ) {
            user.setTaxNumber( userOwnerRequestDto.getTaxNumber() );
        }
        if ( userOwnerRequestDto.getCompanyName() != null ) {
            user.setCompanyName( userOwnerRequestDto.getCompanyName() );
        }
        if ( userOwnerRequestDto.getPhoneNumber() != null ) {
            user.setPhoneNumber( userOwnerRequestDto.getPhoneNumber() );
        }

        return user;
    }
}
