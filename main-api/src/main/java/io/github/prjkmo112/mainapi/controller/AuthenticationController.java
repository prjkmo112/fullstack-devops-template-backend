package io.github.prjkmo112.mainapi.controller;

import io.github.prjkmo112.commonmysqldb.entity.User;
import io.github.prjkmo112.mainapi.dto.UserDto;
import io.github.prjkmo112.mainapi.security.user.UserInfo;
import io.github.prjkmo112.mainapi.security.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    @GetMapping("/check")
    public UserDto check(@UserInfo User user) {
        if (user == null) {
            return UserDto.builder()
                    .isLoginned(false)
                    .build();
        }

        UserDto userDto = UserMapper.INSTANCE.toDto(user);
        userDto.setIsLoginned(true);
        return userDto;
    }


}
