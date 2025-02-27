package com.example.myblog.controller;

import com.example.myblog.config.JwtUtil;
import com.example.myblog.dto.LoginRequest;
import com.example.myblog.dto.SignupRequest;
import com.example.myblog.dto.TokenResponse;
import com.example.myblog.entity.User;
import com.example.myblog.repository.UserRepository;
import com.example.myblog.service.AuthService;
import com.example.myblog.service.OAuth2UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final OAuth2UserService oAuth2UserService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, JwtUtil jwtUtil, OAuth2UserService oAuth2UserService, UserRepository userRepository) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.oAuth2UserService = oAuth2UserService;
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {
        return ResponseEntity.ok(authService.signup(signupRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> request) {
        String newAccessToken = authService.refreshToken(request.get("refreshToken"));
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    /**
     * ✅ 로그아웃 처리 (JWT 기반)
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        try {
            // 🔥 "Bearer " 제거 후 JWT 토큰 추출
            token = token.replace("Bearer ", "").trim();

            // 🔥 JWT 유효성 검증
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("❌ 로그아웃 실패 - 유효하지 않은 토큰입니다.");
            }

            String identifier = jwtUtil.extractUsername(token);

            // 🔥 로그아웃 처리 (Redis에서 Refresh Token 삭제)
            authService.logout(identifier);

            logger.info("✅ 로그아웃 성공 - identifier: {}", identifier);
            return ResponseEntity.ok("로그아웃 성공!");
        } catch (Exception e) {
            logger.error("❌ 로그아웃 실패: {}", e.getMessage());
            return ResponseEntity.status(401).body("❌ 로그아웃 실패 - 잘못된 요청입니다.");
        }
    }

    /**
     * ✅ Google Access Token을 받아 자체 JWT 발급
     */
    @GetMapping("/oauth-success")
    public ResponseEntity<?> oauthSuccess(@RequestHeader("Authorization") String authorizationHeader) {
        System.out.println("✅ [LOG] Authorization Header: " + authorizationHeader);

        try {
            // 🔥 "Bearer " 제거 후 Google Access Token 추출
            String googleAccessToken = authorizationHeader.replace("Bearer ", "").trim();
            System.out.println("✅ [LOG] Extracted Google Access Token: " + googleAccessToken);

            // 🔥 Google Access Token이 올바른지 확인
            if (googleAccessToken.isEmpty() || !googleAccessToken.startsWith("ya")) {
                return ResponseEntity.status(400).body("Invalid Google Access Token format");
            }

            // 🔥 Google API를 사용하여 사용자 정보 가져오기 + JWT 발급
            TokenResponse tokenResponse = oAuth2UserService.loadUserFromGoogle(googleAccessToken);

            System.out.println("✅ [LOG] JWT 발급 완료: " + tokenResponse.getAccessToken());

            // ✅ 응답 반환 (JWT 정보 포함)
            return ResponseEntity.ok(tokenResponse);

        } catch (Exception e) {
            e.printStackTrace(); // ✅ 에러 상세 로그 추가
            System.out.println("❌ [ERROR] " + e.getMessage());
            return ResponseEntity.status(401).body("Invalid Google Access Token: " + e.getMessage());
        }
    }

    /**
     * ✅ 사용자 정보 조회
     */
    @GetMapping("/user")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            token = token.replace("Bearer ", "").trim();

            // 🔥 JWT 유효성 검증
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("❌ 유효하지 않은 토큰입니다.");
            }

            String identifier = jwtUtil.extractUsername(token);
            Set<String> roles = jwtUtil.extractRoles(token);

            // ✅ 일반 로그인은 username으로 찾고, 소셜 로그인은 email로 찾음
            Optional<User> user = userRepository.findByUsername(identifier);
            if (user.isEmpty()) {
                user = userRepository.findByEmail(identifier);  // 🔥 email로도 재확인
            }

            if (user.isEmpty()) {
                return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
            }

            return ResponseEntity.ok(user.get());
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid Token");
        }
    }

}
