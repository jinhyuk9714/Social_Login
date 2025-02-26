package com.example.myblog.service;

import com.example.myblog.entity.User;
import com.example.myblog.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user;

        // 일반 로그인 사용자 (username 기반)
        if (identifier.contains("@")) {
            // 이메일 형식이면 OAuth 로그인 사용자로 간주
            user = userRepository.findByEmail(identifier.toLowerCase())
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + identifier));
        } else {
            // 일반 로그인 사용자 (username 기반)
            user = userRepository.findByUsername(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + identifier));
        }

        // ✅ OAuth2 사용자는 비밀번호 없이 인증
        String password = (user.getPassword() != null) ? user.getPassword() : "";

        return new org.springframework.security.core.userdetails.User(
                user.getEmail() != null ? user.getEmail() : user.getUsername(),
                password,
                Collections.emptyList()
        );
    }

}
