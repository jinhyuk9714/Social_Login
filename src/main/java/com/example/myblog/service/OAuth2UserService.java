package com.example.myblog.service;

import com.example.myblog.config.JwtUtil;
import com.example.myblog.dto.TokenResponse;
import com.example.myblog.entity.User;
import com.example.myblog.repository.UserRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

@Service
public class OAuth2UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate;

    /**
     * ✅ OAuth2UserService 생성자
     * - 필요한 의존성을 주입받음
     * - RestTemplate 객체 생성 (Google API 요청에 사용)
     */
    public OAuth2UserService(UserRepository userRepository, JwtUtil jwtUtil, StringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
        this.restTemplate = new RestTemplate();
    }

    /**
     * ✅ OAuth2 로그인 성공 시 사용자 정보를 저장하거나 업데이트하는 메서드
     * - OAuth2User에서 이메일, 이름, 프로필 이미지를 가져옴
     * - saveOrUpdateUser()를 호출하여 DB에 저장
     * - JWT 토큰을 생성하고 Redis에 저장
     */
    public TokenResponse processOAuthUser(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String profileImage = oAuth2User.getAttribute("picture");

        // 🔥 OAuth2 사용자 저장 또는 업데이트
        User user = saveOrUpdateUser(email, name, profileImage, "google");

        // 🔥 JWT 발급 후 Redis 저장
        return generateAndStoreTokens(user);
    }

    /**
     * ✅ Google Access Token을 사용하여 사용자 정보를 가져오고 로그인 처리
     * - Google API를 호출하여 사용자 정보 가져오기
     * - saveOrUpdateUser()를 호출하여 DB에 저장
     * - JWT 토큰을 생성하고 Redis에 저장
     */
    public TokenResponse loadUserFromGoogle(String googleAccessToken) {
        // 🔍 Google API 요청을 위한 HTTP Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + googleAccessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 🔍 Google API를 호출하여 사용자 정보를 가져옴
        ResponseEntity<Map> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo",
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map<String, Object> userInfo = response.getBody();

        if (userInfo == null || !userInfo.containsKey("email")) {
            throw new RuntimeException("Google 사용자 정보를 가져오지 못했습니다.");
        }

        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String profileImage = (String) userInfo.get("picture");

        // 🔥 OAuth2 사용자 저장 또는 업데이트
        User user = saveOrUpdateUser(email, name, profileImage, "google");

        // 🔥 JWT 발급 후 Redis 저장
        return generateAndStoreTokens(user);
    }

    /**
     * ✅ 사용자 정보를 저장 또는 업데이트하는 메서드
     * - 기존 사용자라면 정보를 업데이트 (username, profileImage)
     * - 신규 사용자라면 새롭게 생성하여 저장
     */
    private User saveOrUpdateUser(String email, String name, String profileImage, String provider) {
        return userRepository.findByEmail(email)
                .map(existingUser -> {
                    // ✅ 기존 사용자 정보 업데이트
                    existingUser.setUsername(name);
                    existingUser.setProfileImage(profileImage);
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    // ✅ 새로운 사용자 생성 및 저장
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(name);
                    newUser.setProfileImage(profileImage);
                    newUser.setOauthProvider(provider);
                    newUser.setRoles(Collections.singleton("ROLE_USER")); // 기본 권한 부여
                    return userRepository.save(newUser);
                });
    }

    /**
     * ✅ JWT 토큰 생성 및 Redis에 저장
     * - Access Token 및 Refresh Token을 생성
     * - Refresh Token을 Redis에 저장하여 세션 관리
     */
    private TokenResponse generateAndStoreTokens(User user) {
        // 🔥 Access Token & Refresh Token 생성
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRoles());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        // 🔥 Redis에 Refresh Token 저장 (기존 값 덮어쓰기)
        String redisKey = "refresh_token:" + user.getEmail();
        redisTemplate.opsForValue().set(redisKey, refreshToken, jwtUtil.getRefreshTokenExpiration(), TimeUnit.MILLISECONDS);

        return new TokenResponse(accessToken, refreshToken);
    }
}
