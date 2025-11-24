package io.github.prjkmo112.mainapi.security;

import io.github.prjkmo112.common.constants.HttpHeaderConstant;
import io.github.prjkmo112.common.funcs.HttpRequestFunc;
import io.github.prjkmo112.commoneventlogger.EventLoggerBase;
import io.github.prjkmo112.commonmysqldb.entity.User;
import io.github.prjkmo112.mainapi.security.user.UserBean;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;

public class AuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private final AuthTokenService authTokenService;

    private final EventLoggerBase eventLoggerBase;

    public AuthenticationProcessingFilter(
            String defaultFilterProcessesUrl,
            AuthTokenService authTokenService,
            EventLoggerBase eventLoggerBase
    ) {
        super(defaultFilterProcessesUrl);

        this.authTokenService = authTokenService;
        this.eventLoggerBase = eventLoggerBase;

        RequestMatcher matcher = PathPatternRequestMatcher
                .withDefaults()
                .matcher(defaultFilterProcessesUrl);
        super.setRequiresAuthenticationRequestMatcher(matcher);
        super.setAuthenticationSuccessHandler(this::onAuthenticationSuccess);
    }

    private void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication auth
    ) throws IOException, ServletException {
        String context = request.getContextPath();
        String fullUrl = request.getRequestURI();
        String url = fullUrl.substring(fullUrl.indexOf(context) + context.length());
        request.getRequestDispatcher(url).forward(request, response);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        req.setAttribute("AUTH_FILTER_APPLIED", Boolean.TRUE);
        String token = authTokenService.extractToken(req);

        if (token == null || token.isEmpty())
            return null;

        AbstractAuthenticationToken authenticationToken = absAuthToken(token);
        if (authenticationToken == null) {
            authTokenService.setTokenToCookie(res, token, 0);
            throw new BadCredentialsException("Invalid token");
        } else if (!authTokenService.isValidate(token)) {
            authTokenService.setTokenToCookie(res, authTokenService.refreshToken(token), authTokenService.getTokenExpiration());
        }

        return authenticationToken;
    }

    private AbstractAuthenticationToken absAuthToken(String token) {
        if (!authTokenService.isValidate(token))
            return null;

        try {
            User user = authTokenService.getUser(token);
            UserBean userBean = new UserBean(user);
            return new UsernamePasswordAuthenticationToken(user, token, userBean.getAuthorities());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain next) throws IOException, ServletException {
        if (req.getAttribute("AUTH_FILTER_APPLIED") != null)
            next.doFilter(req, res);
        else
            super.doFilter(req, res, next);
    }

    public void handleLoginSuccess(
            HttpServletRequest req,
            HttpServletResponse res,
            Authentication auth
    ) throws IOException {
        String token = authTokenService.generateToken(auth);

        EventLoggerBase.Dto dto = EventLoggerBase.Dto.builder()
                .key("authentication-processing-filter")
                .eventType("user-login")
                .ipAddress(HttpRequestFunc.getClientIp(req))
                .userId(String.valueOf(((UserBean) auth.getPrincipal()).getUserId()))
                .value("User logged in")
                .build();

        eventLoggerBase.messageSync(dto);

        authTokenService.setTokenToCookie(res, token, authTokenService.getTokenExpiration());

        res.getWriter().write(token);
        res.getWriter().flush();
    }

    public void handleLogoutSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth) throws IOException {
        Cookie cookie = null;
        Cookie[] cookies = req.getCookies();
        for (Cookie c : cookies) {
            if (c.getName().equals(HttpHeaderConstant.AUTH_COOKIE_NAME)) {
                cookie = c;
                break;
            }
        }

        if (cookie != null) {
            cookie.setMaxAge(0);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            res.addCookie(cookie);
        }
    }
}
