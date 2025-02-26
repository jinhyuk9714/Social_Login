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

    public OAuth2UserService(UserRepository userRepository, JwtUtil jwtUtil, StringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
        this.restTemplate = new RestTemplate();
    }

    /**
     * ✅ OAuth2 로그인 사용자를 저장 또는 업데이트 (OAuth2User 기반)
     */
    public TokenResponse processOAuthUser(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String profileImage = oAuth2User.getAttribute("picture");

        User user = saveOrUpdateUser(email, name, profileImage, "google");

        return generateAndStoreTokens(user);
    }

    /**
     * ✅ Google Access Token을 사용하여 사용자 정보를 가져온 후 로그인 처리
     */
    public TokenResponse loadUserFromGoogle(String googleAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + googleAccessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
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

        User user = saveOrUpdateUser(email, name, profileImage, "google");

        return generateAndStoreTokens(user);
    }

    /**
     * ✅ 사용자 정보를 저장 또는 업데이트하는 메서드
     */
    private User saveOrUpdateUser(String email, String name, String profileImage, String provider) {
        return userRepository.findByEmail(email)
                .map(existingUser -> {
                    existingUser.setUsername(name);
                    existingUser.setProfileImage(profileImage);
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(name);
                    newUser.setProfileImage(profileImage);
                    newUser.setOauthProvider(provider);
                    newUser.setRoles(Collections.singleton("ROLE_USER"));
                    return userRepository.save(newUser);
                });
    }

    /**
     * ✅ JWT 토큰 생성 및 Redis에 저장
     */
    private TokenResponse generateAndStoreTokens(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRoles());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        String redisKey = "refresh_token:" + user.getEmail();
        redisTemplate.opsForValue().set(redisKey, refreshToken, jwtUtil.getRefreshTokenExpiration(), TimeUnit.MILLISECONDS);

        return new TokenResponse(accessToken, refreshToken);
    }
}
