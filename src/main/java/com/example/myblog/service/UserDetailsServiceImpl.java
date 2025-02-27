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

    /**
     * ✅ UserDetailsServiceImpl 생성자
     * - UserRepository를 주입받아 DB에서 사용자 정보를 조회
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * ✅ 사용자 인증 정보 조회 (Spring Security가 자동 호출)
     * - 일반 로그인: username 기반 조회
     * - 소셜 로그인(OAuth2): email 기반 조회
     *
     * @param identifier username 또는 email
     * @return UserDetails (Spring Security에서 사용)
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우 예외 발생
     */
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user;

        // 🔍 identifier(아이디)가 이메일인지 확인
        if (identifier.contains("@")) {
            // ✅ 이메일 기반 조회 (OAuth2 로그인 사용자)
            user = userRepository.findByEmail(identifier.toLowerCase())
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + identifier));
        } else {
            // ✅ 일반 로그인 사용자는 username 기반 조회
            user = userRepository.findByUsername(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + identifier));
        }

        // 🔥 OAuth2 사용자는 비밀번호 없이 인증 처리 가능 (비밀번호 없을 경우 빈 문자열 처리)
        String password = (user.getPassword() != null) ? user.getPassword() : "";

        // ✅ Spring Security의 User 객체를 생성하여 반환
        return new org.springframework.security.core.userdetails.User(
                user.getEmail() != null ? user.getEmail() : user.getUsername(), // OAuth 사용자는 email을 기본값으로 사용
                password,
                Collections.emptyList() // 🔥 권한(roles)이 필요하면 여기에 추가 가능
        );
    }
}
