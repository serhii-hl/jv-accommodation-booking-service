package app.mapper.impl;

import app.dto.user.CreateUserOwnerRequestDto;
import app.dto.user.CreateUserRequestDto;
import app.dto.user.OwnerProfileDto;
import app.dto.user.UserDto;
import app.dto.user.UserProfileDto;
import app.mapper.UserMapper;
import app.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-08T10:03:07+0300",
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

        userDto.setId( user.getId() );
        if ( user.getEmail() != null ) {
            userDto.setEmail( user.getEmail() );
        }
        if ( user.getFirstName() != null ) {
            userDto.setFirstName( user.getFirstName() );
        }
        if ( user.getLastName() != null ) {
            userDto.setLastName( user.getLastName() );
        }
        if ( user.getPhoneNumber() != null ) {
            userDto.setPhoneNumber( user.getPhoneNumber() );
        }

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

    @Override
    public UserProfileDto toUserProfileDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserProfileDto userProfileDto = new UserProfileDto();

        if ( user.getEmail() != null ) {
            userProfileDto.setEmail( user.getEmail() );
        }
        if ( user.getFirstName() != null ) {
            userProfileDto.setFirstName( user.getFirstName() );
        }
        if ( user.getLastName() != null ) {
            userProfileDto.setLastName( user.getLastName() );
        }
        if ( user.getRole() != null ) {
            userProfileDto.setRole( user.getRole() );
        }
        if ( user.getPhoneNumber() != null ) {
            userProfileDto.setPhoneNumber( user.getPhoneNumber() );
        }

        return userProfileDto;
    }

    @Override
    public OwnerProfileDto toOwnerProfileDto(User user) {
        if ( user == null ) {
            return null;
        }

        OwnerProfileDto ownerProfileDto = new OwnerProfileDto();

        if ( user.getEmail() != null ) {
            ownerProfileDto.setEmail( user.getEmail() );
        }
        if ( user.getFirstName() != null ) {
            ownerProfileDto.setFirstName( user.getFirstName() );
        }
        if ( user.getLastName() != null ) {
            ownerProfileDto.setLastName( user.getLastName() );
        }
        if ( user.getRole() != null ) {
            ownerProfileDto.setRole( user.getRole() );
        }
        if ( user.getCompanyName() != null ) {
            ownerProfileDto.setCompanyName( user.getCompanyName() );
        }
        if ( user.getTaxNumber() != null ) {
            ownerProfileDto.setTaxNumber( user.getTaxNumber() );
        }
        if ( user.getPhoneNumber() != null ) {
            ownerProfileDto.setPhoneNumber( user.getPhoneNumber() );
        }

        return ownerProfileDto;
    }
}
