package vsu.csf.rentyserver.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import vsu.csf.rentyserver.configuration.properties.SecurityProperties;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;
    private final SecurityProperties securityProperties;

    private JwtParser jwtParser;

    @PostConstruct
    private void setUp() {
        jwtParser = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build();
    }

    private Key getSecretKey() {
        return Keys.hmacShaKeyFor(Base64.decodeBase64(securityProperties.secretKey()));
    }

    public String createToken(String username, String role) {
        log.info("Token created for user {}", username);

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role);
        Date now = new Date();
        Date validity = new Date(now.getTime() + securityProperties.jwtExpiresAfter().toMillis());

        return "Bearer %s".formatted(Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(getSecretKey())
                .compact());
    }

    public boolean validateToken(String token) {
        Jws<Claims> claimsJws = jwtParser
                .parseClaimsJws(token);
        return !claimsJws.getBody().getExpiration().before(new Date());
    }

    public Authentication getAuthentication(String token) {
        var userDetails = userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return jwtParser.parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null) {
            return null;
        }
        return header.substring("Bearer ".length());
    }
}
