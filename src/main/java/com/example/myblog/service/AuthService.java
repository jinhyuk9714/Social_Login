package com.example.myblog.service;

import com.example.myblog.dto.SignupRequest;
import com.example.myblog.dto.LoginRequest;
import com.example.myblog.entity.User;
import com.example.myblog.repository.UserRepository;
import com.example.myblog.config.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String signup(SignupRequest signupRequest) {
        Optional<User> existingUser = userRepository.findByUsername(signupRequest.getUsername());
        if (existingUser.isPresent()) {
            throw new RuntimeException("이미 존재하는 사용자명입니다.");
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword())); // 비밀번호 암호화
        user.setEmail(signupRequest.getEmail());

        // ✅ 사용자가 roles를 보내지 않았으면 "ROLE_USER" 기본값 설정
        Set<String> roles = signupRequest.getRoles() != null ? new HashSet<>(signupRequest.getRoles()) : new HashSet<>();
        if (roles.isEmpty()) {
            roles.add("ROLE_USER");
        }
        user.setRoles(roles);

        userRepository.save(user);
        return "회원가입 성공!";
    }

    public String login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            return jwtUtil.generateToken(user.getUsername(), user.getRoles());
        } catch (Exception e) {
            throw new RuntimeException("로그인 실패: 아이디 또는 비밀번호를 확인하세요.");
        }
    }
}
