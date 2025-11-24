package io.github.prjkmo112.mainapi.config;

import io.github.prjkmo112.commoneventlogger.EventLoggerBase;
import io.github.prjkmo112.mainapi.security.AuthTokenService;
import io.github.prjkmo112.mainapi.security.AuthenticationProcessingFilter;
import io.github.prjkmo112.mainapi.security.user.UserDetailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthTokenService authTokenService;
    private final UserDetailService userDetailService;
    private final EventLoggerBase eventLoggerBase;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Value("${web.cors.header:*}")
    private String corsAllowHeader;

    @Value("${web.cors.method:*}")
    private String corsAllowMethod;

    @Value("${web.cors.origin:}")
    private String corsAllowOrigin;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userDetailService)
                .passwordEncoder(passwordEncoder());

        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        AuthenticationProcessingFilter apiFilter = new AuthenticationProcessingFilter(
                apiPrefix + "/**",
                authTokenService,
                eventLoggerBase
        );

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationManager(authenticationManager)
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                        .requestMatchers(apiPrefix + "/login", apiPrefix + "/auth/**", "/user/**").permitAll()
//                        .requestMatchers(apiPrefix).hasRole(UserRoleEnum.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .addFilterBefore(apiFilter, BasicAuthenticationFilter.class)
                .formLogin(login ->
                        login.failureHandler(this::handleLoginFail)
                                .successHandler(apiFilter::handleLoginSuccess)
                                .loginPage(apiPrefix + "/login")
                )
                .logout(configurer ->
                        configurer
                                .logoutUrl(apiPrefix + "/logout")
                                .logoutSuccessHandler(apiFilter::handleLogoutSuccess)
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader(corsAllowHeader);
        configuration.addAllowedMethod(corsAllowMethod);
        configuration.addAllowedOrigin(corsAllowOrigin);
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private void handleLoginFail(HttpServletRequest req, HttpServletResponse res, AuthenticationException e) {
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}
