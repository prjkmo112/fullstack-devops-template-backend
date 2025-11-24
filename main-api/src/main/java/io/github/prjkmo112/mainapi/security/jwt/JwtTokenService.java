package io.github.prjkmo112.mainapi.security.jwt;

import io.github.prjkmo112.commonmysqldb.entity.User;
import io.github.prjkmo112.commonmysqldb.repository.UserRepository;
import io.github.prjkmo112.mainapi.security.user.UserBean;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtTokenService {

    @Getter
    @Value("${jwt.expiration}")
    private int expiration;

    private final SecretKey secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.subject_seperator}")
    private String subjectSeperator;

    private final JwtParser jwtParser;
    private final UserRepository userRepository;

    public JwtTokenService(
            @Value("${jwt.secret}") String secretKey,
            UserRepository userRepository
    ) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.jwtParser = Jwts.parser().verifyWith(this.secretKey).build();
        this.userRepository = userRepository;
    }

    public String generateToken(UserBean userBean) {
        return issueToken(userBean.getEmail() + subjectSeperator + userBean.getUsername());
    }

    public String refreshToken(String token) {
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();
        if ((claims.getExpiration().getTime() - new Date().getTime()) > 0) {
            return token;
        }
        return issueToken(claims.getSubject());
    }

    public boolean validateToken(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }

        try {
            jwtParser.parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private String issueToken(String subject) {
        return Jwts.builder()
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                .signWith(secretKey, Jwts.SIG.HS512)
                .subject(subject)
                .compact();
    }

    public Optional<User> getUser(String token) {
        String[] jwtSubject = jwtParser
                .parseSignedClaims(token)
                .getPayload()
                .getSubject()
                .split(subjectSeperator);
        String email = jwtSubject[0];
        return userRepository.findByEmail(email);
    }
}
