package app.dto.user;

import app.validation.user.Email;
import app.validation.user.FieldMatch;
import app.validation.user.Name;
import app.validation.user.Password;
import app.validation.user.Phone;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@FieldMatch(first = "password", second = "confirmPassword", message = "Passwords do not match")
public class CreateUserOwnerRequestDto {
    @Email
    @NotBlank
    private String email;
    @Name
    @NotBlank
    private String firstName;
    @Name
    @NotBlank
    private String lastName;
    @Password
    @NotBlank
    private String password;
    @NotBlank
    private String confirmPassword;
    @Phone
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String taxNumber;
    @NotBlank
    private String companyName;
}
