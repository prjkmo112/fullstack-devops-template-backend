package io.github.prjkmo112.mainapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDto {

    @NotNull
    @NotBlank
    @Size(min = 2, max = 128)
    String name;

    @NotNull
    @NotBlank
    @Email
    @Size(min = 6, max = 128)
    String email;

    @NotNull
    @NotBlank
    @Size(min = 6, max = 255)
    String passwd;

}
