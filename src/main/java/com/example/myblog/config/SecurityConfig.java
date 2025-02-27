package com.example.myblog.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

/**
 * ✅ Spring Security 설정 클래스
 *
 * - JWT 기반 인증 & OAuth2 로그인을 지원
 * - Swagger API 문서에서 Bearer 토큰을 사용할 수 있도록 설정
 * - CSRF 비활성화 및 경로별 접근 권한 설정 포함
 */
@Configuration
@EnableWebSecurity
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * ✅ SecurityConfig 생성자
     *
     * @param jwtUtil          JWT 관련 유틸리티 클래스
     * @param userDetailsService 사용자 정보를 로드하는 서비스
     */
    public SecurityConfig(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * ✅ 비밀번호 암호화 설정
     *
     * - BCryptPasswordEncoder를 사용하여 비밀번호를 해싱
     * - 강도(strength) 값은 10으로 설정
     *
     * @return PasswordEncoder 객체
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * ✅ 인증 관리자 설정 (AuthenticationManager)
     *
     * - DaoAuthenticationProvider를 사용하여 인증 처리
     * - UserDetailsService와 PasswordEncoder를 설정
     *
     * @return AuthenticationManager 객체
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(List.of(authProvider));
    }

    /**
     * ✅ Spring Security의 필터 체인 설정
     *
     * - JWT 기반 인증을 사용하며, OAuth2 로그인도 지원
     * - 특정 경로에 대한 접근 제어 설정 포함
     * - JWT 필터를 UsernamePasswordAuthenticationFilter 전에 실행
     *
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain 객체
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtFilter jwtFilter = new JwtFilter(jwtUtil, userDetailsService);

        http
                // ✅ CSRF 비활성화 (JWT 사용 시 필요 없음)
                .csrf(csrf -> csrf.disable())

                // ✅ 요청별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/auth/oauth-success").permitAll() // ✅ 403 문제 해결
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // ✅ ADMIN만 접근 가능
                        .anyRequest().authenticated() // ✅ 나머지는 인증 필요
                )

                // ✅ OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(auth -> auth
                                .authorizationRequestRepository(authorizationRequestRepository()) // ✅ 인증 요청 저장소 추가
                        )
                        .defaultSuccessUrl("/api/auth/oauth-success", true) // ✅ 로그인 성공 후 리디렉트
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(new OidcUserService()) // ✅ OpenID Connect 지원
                                .userService(new DefaultOAuth2UserService()) // ✅ 일반 OAuth2 지원
                        )
                )

                // ✅ 세션 관리 설정 (OAuth2 로그인 시 세션 필요)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                // ✅ JWT 필터 등록 (UsernamePasswordAuthenticationFilter 전에 실행)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // ✅ 예외 처리 핸들링 (403 & 인증 실패 처리)
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDeniedHandler()) // ✅ 403 발생 시 로그 남김
                        .authenticationEntryPoint(new Http403ForbiddenEntryPoint()) // 403 진입 로그
                );

        return http.build();
    }

    /**
     * ✅ 403 (Forbidden) 예외 발생 시 핸들러
     *
     * - 권한이 없는 사용자가 접근할 경우, 403 상태 코드와 메시지를 반환
     *
     * @return AccessDeniedHandler 객체
     */
    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            System.out.println("❌ [403 ERROR] 접근이 거부되었습니다: " + request.getRequestURI());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
        };
    }

    /**
     * ✅ OAuth2 인증 요청을 저장하는 객체
     *
     * - 인증 요청을 세션에 저장하여 OAuth2 로그인 후에도 유지 가능
     *
     * @return HttpSessionOAuth2AuthorizationRequestRepository 객체
     */
    @Bean
    public HttpSessionOAuth2AuthorizationRequestRepository authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }
}
