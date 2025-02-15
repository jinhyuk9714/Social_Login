package com.example.myblog.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final String SECRET_KEY = EnvConfig.get("JWT_SECRET_KEY");
    private final long EXPIRATION_TIME = Long.parseLong(EnvConfig.get("JWT_EXPIRATION_TIME"));

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // ✅ JWT 생성 시 roles를 List<String>으로 변환하여 저장
    public String generateToken(String username, Set<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", new ArrayList<>(roles)) // ✅ List<String>으로 변환
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // ✅ JWT에서 roles를 List<String>으로 변환 후 Set<String>으로 변환
    public Set<String> extractRoles(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Object rolesObject = claims.get("roles");
        if (rolesObject instanceof List<?>) {
            List<?> rolesList = (List<?>) rolesObject;
            return rolesList.stream()
                    .filter(String.class::isInstance) // 문자열인지 확인
                    .map(String.class::cast) // 문자열로 변환
                    .collect(Collectors.toSet()); // ✅ Set<String>으로 변환
        }
        return new HashSet<>();
    }
}
