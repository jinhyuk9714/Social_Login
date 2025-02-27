package com.example.myblog.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ✅ JwtUtil (JWT 관리 유틸리티)
 *
 * - JWT 토큰 생성 (Access Token, Refresh Token)
 * - JWT 검증 및 파싱 (사용자 정보, 역할 추출)
 * - 서명 키 관리 (HMAC SHA256)
 * - 만료 시간 설정 (Access: 15분, Refresh: 7일)
 */
@Component
public class JwtUtil {

    // ✅ 환경 변수에서 JWT 비밀 키 로드 (Base64 인코딩된 값)
    private final String SECRET_KEY = EnvConfig.get("JWT_SECRET_KEY");

    // ✅ 토큰 만료 시간 설정 (단위: 밀리초)
    private final long ACCESS_TOKEN_EXPIRATION = 15 * 60 * 1000;  // 15분
    private final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000;  // 7일

    /**
     * ✅ 서명 키 생성 (HMAC SHA256)
     *
     * - SECRET_KEY를 Base64 디코딩 후 HMAC SHA256 키로 변환
     * - JWT 서명을 생성 및 검증하는데 사용됨
     */
    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY); // Base64 디코딩
        return Keys.hmacShaKeyFor(keyBytes); // HMAC SHA256 키 생성
    }

    /**
     * ✅ Access Token 생성
     *
     * @param username 사용자명 (이메일 또는 ID)
     * @param roles 사용자의 역할 (예: ROLE_USER, ROLE_ADMIN)
     * @return 생성된 Access Token 문자열 (JWT)
     */
    public String generateAccessToken(String username, Set<String> roles) {
        return Jwts.builder()
                .setSubject(username)  // 사용자 식별 값 (예: 이메일)
                .claim("roles", roles)  // 사용자의 역할(권한) 추가
                .setIssuedAt(Date.from(Instant.now())) // 토큰 발급 시간
                .setExpiration(Date.from(Instant.now().plusMillis(ACCESS_TOKEN_EXPIRATION))) // 만료 시간
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // HMAC SHA256 서명 적용
                .compact(); // JWT 문자열 생성
    }

    /**
     * ✅ Refresh Token 생성
     *
     * - Refresh Token은 역할 정보 없이 사용자명만 저장
     * - Access Token을 재발급할 때 사용됨
     *
     * @param username 사용자명
     * @return 생성된 Refresh Token (JWT)
     */
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username) // 사용자명 저장
                .setIssuedAt(Date.from(Instant.now())) // 발급 시간
                .setExpiration(Date.from(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION))) // 만료 시간 (7일)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 서명
                .compact();
    }

    /**
     * ✅ 토큰에서 사용자명(이메일 또는 ID) 추출
     *
     * @param token JWT 문자열
     * @return 사용자명 (토큰의 Subject 필드 값)
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // 서명 키 설정
                .build()
                .parseClaimsJws(token) // 토큰 파싱
                .getBody()
                .getSubject(); // Subject 값 반환 (username)
    }

    /**
     * ✅ JWT에서 역할(Role) 정보를 추출하여 Set<String>으로 반환
     *
     * @param token JWT 문자열
     * @return 사용자의 역할 목록 (예: ["ROLE_USER", "ROLE_ADMIN"])
     */
    public Set<String> extractRoles(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // 서명 키 설정
                .build()
                .parseClaimsJws(token) // 토큰 파싱
                .getBody(); // Claims(토큰 정보) 추출

        Object rolesObject = claims.get("roles"); // "roles" 필드 값 가져오기
        if (rolesObject instanceof Collection<?>) {
            return ((Collection<?>) rolesObject).stream()
                    .map(Object::toString) // 문자열로 변환
                    .collect(Collectors.toSet());
        }
        return new HashSet<>(); // 역할 정보가 없으면 빈 Set 반환
    }

    /**
     * ✅ 토큰 유효성 검증
     *
     * - JWT의 서명을 확인하고 만료 여부 검사
     * - 올바른 형식과 서명을 가진 토큰만 유효한 것으로 간주
     *
     * @param token 검증할 JWT 문자열
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // 서명 키 설정
                    .build()
                    .parseClaimsJws(token); // 토큰 파싱 (유효하지 않으면 예외 발생)
            return true; // 유효한 토큰
        } catch (JwtException e) {
            return false; // 유효하지 않은 토큰
        }
    }

    /**
     * ✅ 리프레시 토큰의 유효 시간(밀리초) 반환
     *
     * @return 리프레시 토큰 만료 시간 (7일)
     */
    public long getRefreshTokenExpiration() {
        return REFRESH_TOKEN_EXPIRATION;
    }
}
