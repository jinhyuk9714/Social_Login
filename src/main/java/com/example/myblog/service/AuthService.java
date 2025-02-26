package com.example.myblog.service;

import com.example.myblog.dto.LoginRequest;
import com.example.myblog.dto.SignupRequest;
import com.example.myblog.dto.TokenResponse;
import com.example.myblog.entity.User;
import com.example.myblog.config.JwtUtil;
import com.example.myblog.repository.UserRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, StringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 회원가입 메서드
     */
    public String signup(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new RuntimeException("이미 존재하는 사용자명입니다.");
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword())); // 비밀번호 암호화
        user.setEmail(signupRequest.getEmail());

        // 기본 역할 설정 (ROLE_USER)
        Set<String> roles = signupRequest.getRoles() != null ? new HashSet<>(signupRequest.getRoles()) : new HashSet<>();
        if (roles.isEmpty()) {
            roles.add("ROLE_USER");
        }
        user.setRoles(roles);

        userRepository.save(user);
        return "회원가입 성공!";
    }

    /**
     * 로그인 메서드
     */
    public TokenResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRoles());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // 🔥 기존 토큰 삭제 후 새로운 리프레시 토큰 저장
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String redisKey = "refresh_token:" + user.getUsername();
        redisTemplate.delete(redisKey);  // ✅ 기존 토큰 삭제
        ops.set(redisKey, refreshToken, Duration.ofMillis(jwtUtil.getRefreshTokenExpiration()));

        return new TokenResponse(accessToken, refreshToken);
    }

    /**
     * ✅ 소셜 로그인 후 JWT 토큰 발급 및 Redis에 Refresh Token 저장
     */
    public void processOAuthLogin(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRoles());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        // 🔥 Redis에 Refresh Token 저장 (소셜 로그인 사용자는 이메일 기준)
        String redisKey = "refresh_token:" + user.getEmail();
        redisTemplate.opsForValue().set(redisKey, refreshToken, jwtUtil.getRefreshTokenExpiration(), TimeUnit.MILLISECONDS);
    }


    /**
     * 리프레시 토큰으로 새로운 액세스 토큰 발급
     */
    public String refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("리프레시 토큰이 유효하지 않습니다.");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // Redis에서 저장된 리프레시 토큰 가져오기
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String redisKey = "refresh_token:" + user.getUsername();
        String storedToken = ops.get(redisKey);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new RuntimeException("리프레시 토큰이 존재하지 않거나 만료되었습니다.");
        }

        return jwtUtil.generateAccessToken(user.getUsername(), user.getRoles());
    }

    /**
     * 로그아웃 메서드 (Redis에서 리프레시 토큰 삭제)
     */
    public void logout(String identifier) {
        logger.info("🔍 로그아웃 요청 - identifier: {}", identifier);

        // 일반 로그인: username으로 찾고, 없으면 소셜 로그인(email)으로 찾기
        User user = userRepository.findByUsername(identifier)
                .orElseGet(() -> userRepository.findByEmail(identifier)
                        .orElseThrow(() -> new RuntimeException("❌ 로그아웃 실패 - 사용자를 찾을 수 없습니다. identifier: " + identifier)));

        logger.info("✅ 로그아웃 성공 - username: {}", user.getUsername());

        // 🔥 Redis Key 생성: 일반 로그인은 username 기반, 소셜 로그인은 email 기반
        String redisKey;
        if (user.getOauthProvider() != null) { // 소셜 로그인 사용자인 경우
            redisKey = "refresh_token:" + user.getEmail();
        } else { // 일반 로그인 사용자
            redisKey = "refresh_token:" + user.getUsername();
        }

        // 🔥 Redis에서 Refresh Token 삭제
        Boolean deleted = redisTemplate.delete(redisKey);

        if (Boolean.TRUE.equals(deleted)) {
            logger.info("✅ 로그아웃 성공 - Refresh Token 삭제됨: {}", redisKey);
        } else {
            logger.warn("⚠️ 로그아웃 실패 - Redis에서 삭제되지 않음: {}", redisKey);
        }
    }


}
