package io.github.prjkmo112.mainapi.controller.external;

import io.github.prjkmo112.commonmysqldb.entity.User;
import io.github.prjkmo112.commonmysqldb.entity.UserRoleEnum;
import io.github.prjkmo112.mainapi.dto.CreateUserDto;
import io.github.prjkmo112.mainapi.dto.UserDto;
import io.github.prjkmo112.mainapi.security.user.UserMapper;
import io.github.prjkmo112.mainapi.security.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public UserDto register(
            @RequestBody @Valid CreateUserDto createUserDto,
            HttpServletRequest request
    ) {
        User user = User.builder()
                .name(createUserDto.getName())
                .email(createUserDto.getEmail())
                .passwd(passwordEncoder.encode(createUserDto.getPasswd()))
                .role(UserRoleEnum.USER)
                .build();

        userService.registerUser(user, request);
        return UserMapper.INSTANCE.toDto(user);
    }
}
