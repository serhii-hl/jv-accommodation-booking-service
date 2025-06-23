package app.dto.user;

import app.validation.user.NewPassword;
import app.validation.user.NewPhone;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileInformationDto implements ProfileDto {
    @NewPassword
    private String password;
    private String firstName;
    private String lastName;
    @NewPhone
    private String phoneNumber;
}
