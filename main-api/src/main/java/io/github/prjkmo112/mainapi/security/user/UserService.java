package io.github.prjkmo112.mainapi.security.user;

import io.github.prjkmo112.common.funcs.HttpRequestFunc;
import io.github.prjkmo112.commoneventlogger.EventLoggerBase;
import io.github.prjkmo112.commonmysqldb.entity.User;
import io.github.prjkmo112.commonmysqldb.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final EventLoggerBase eventLoggerBase;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                    .orElse(null);
    }

    public void registerUser(User user, HttpServletRequest request) {
        User checkUser = getUserByEmail(user.getEmail());

        if (checkUser == null) {
            User insertedUser = userRepository.save(user);

            EventLoggerBase.Dto dto = EventLoggerBase.Dto.builder()
                    .key("user-service")
                    .eventType("user-register")
                    .ipAddress(HttpRequestFunc.getClientIp(request))
                    .userId(insertedUser.getId().toString())
                    .value("User registered: " + user.getEmail())
                    .build();

            eventLoggerBase.messageSync(dto);
        } else {
            EventLoggerBase.Dto dto = EventLoggerBase.Dto.builder()
                    .key("user-service")
                    .eventType("user-register")
                    .ipAddress(HttpRequestFunc.getClientIp(request))
                    .userId(checkUser.getId().toString())
                    .value("Existed User try to register: " + user.getEmail())
                    .build();

            eventLoggerBase.messageSync(dto);

            throw new IllegalArgumentException("User already exists");
        }
    }
}
