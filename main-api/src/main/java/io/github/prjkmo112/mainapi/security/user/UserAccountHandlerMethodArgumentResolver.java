package io.github.prjkmo112.mainapi.security.user;

import io.github.prjkmo112.commonmysqldb.entity.User;
import io.github.prjkmo112.commonmysqldb.repository.UserRepository;
import io.github.prjkmo112.mainapi.security.AuthTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserAccountHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;
    private final AuthTokenService authTokenService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(UserAccount.class) != null && parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {
        if (supportsParameter(parameter)) {
            Object principal = webRequest.getUserPrincipal();
            if (principal == null) {
                principal = authTokenService.extractToken(((ServletWebRequest) webRequest).getRequest());
            }
            if (principal == null) {
                return null;
            }

            Object userBean = null;
            if (principal instanceof Authentication) {
                userBean = ((Authentication) principal).getPrincipal();
            }

            return userRepository.findByEmail(((User) Objects.requireNonNull(userBean)).getEmail()).orElse(null);
        } else {
            return WebArgumentResolver.UNRESOLVED;
        }
    }
}
