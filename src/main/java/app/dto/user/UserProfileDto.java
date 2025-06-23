package app.dto.user;

import app.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileDto implements ProfileDto {
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private String phoneNumber;
}
