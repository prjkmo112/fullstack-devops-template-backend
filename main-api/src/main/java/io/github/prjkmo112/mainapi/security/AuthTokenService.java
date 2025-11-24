package io.github.prjkmo112.mainapi.security;

import io.github.prjkmo112.common.constants.HttpHeaderConstant;
import io.github.prjkmo112.commonmysqldb.entity.User;
import io.github.prjkmo112.mainapi.security.jwt.JwtTokenService;
import io.github.prjkmo112.mainapi.security.user.UserBean;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

@Service
@RequiredArgsConstructor
public class AuthTokenService {
    private final JwtTokenService jwtTokenService;

    public String extractToken(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, HttpHeaderConstant.AUTH_COOKIE_NAME);
        String cookieToken = cookie != null ? cookie.getValue() : null;
        return cookieToken != null ? cookieToken : request.getHeader(HttpHeaderConstant.AUTH_COOKIE_NAME);
    }

    public String generateToken(Authentication authentication) {
        if (authentication == null)
            return null;

        UserBean user;
        if (authentication.getPrincipal() instanceof UserBean)
            user = (UserBean) authentication.getPrincipal();
        else
            return null;

        return jwtTokenService.generateToken(user);
    }

    public void setTokenToCookie(HttpServletResponse response, String token, int maxAge) {
        Cookie cookie = new Cookie(HttpHeaderConstant.AUTH_COOKIE_NAME, token);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");

        response.addCookie(cookie);
    }

    public String refreshToken(String token) {
        return jwtTokenService.refreshToken(token);
    }

    public int getTokenExpiration() {
        return jwtTokenService.getExpiration();
    }

    public User getUser(String token) {
        return jwtTokenService.getUser(token).orElse(null);
    }

    public boolean isValidate(String token) {
        return jwtTokenService.validateToken(token);
    }
}
