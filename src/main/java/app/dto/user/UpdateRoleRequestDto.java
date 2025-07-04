package app.dto.user;

import app.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRoleRequestDto {
    @NotNull
    private boolean isDeleted;
    @NotBlank
    private Role role;
}
