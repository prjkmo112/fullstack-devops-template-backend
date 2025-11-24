package io.github.prjkmo112.mainapi.dto;

import io.github.prjkmo112.commonmysqldb.entity.UserRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link io.github.prjkmo112.commonmysqldb.entity.User}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {
    @NotNull
    @NotBlank
    @Size(min = 2, max = 255)
    String name;

    @NotNull
    @NotBlank
    @Email
    @Size(max = 128)
    String email;

    UserRoleEnum role;

    @Builder.Default
    Boolean isLoginned = false;
}
