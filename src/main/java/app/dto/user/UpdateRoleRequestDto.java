package app.dto.user;

import app.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRoleRequestDto {
    private boolean isDeleted;
    @NotNull
    private Role role;
}
