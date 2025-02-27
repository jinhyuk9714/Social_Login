package com.example.myblog.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ✅ JwtFilter (JWT 인증 필터)
 *
 * - 모든 요청에서 JWT를 검증하여 사용자 인증을 수행하는 필터
 * - `OncePerRequestFilter`를 상속받아 한 요청당 한 번 실행됨
 * - JWT 토큰을 검증하고, 유효하면 SecurityContextHolder에 인증 정보를 저장
 */
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;  // JWT 유틸리티 (토큰 생성/검증 기능)
    private final UserDetailsService userDetailsService; // 사용자 정보를 로드하는 서비스

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class); // 로그 기록용

    /**
     * ✅ JwtFilter 생성자
     *
     * @param jwtUtil JWT 유틸리티 (토큰 생성 및 검증)
     * @param userDetailsService Spring Security의 UserDetailsService (사용자 정보 로드)
     */
    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * ✅ 요청마다 실행되는 JWT 인증 필터
     *
     * - HTTP 요청의 Authorization 헤더에서 JWT 토큰을 추출
     * - 토큰이 유효하면 사용자를 인증하고 SecurityContextHolder에 저장
     * - 예외 발생 시, 적절한 HTTP 응답을 반환
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param chain 필터 체인
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 🔍 요청 URI 로깅 (디버깅용)
        logger.debug("🔍 요청 URI: {}", request.getRequestURI());

        // ✅ OAuth2 로그인 관련 엔드포인트는 필터 제외 (OAuth2 로그인은 JWT를 사용하지 않음)
        if (request.getRequestURI().startsWith("/api/auth/oauth-success")) {
            logger.debug("⏩ OAuth2 로그인 요청 - 필터 패스");
            chain.doFilter(request, response);
            return;
        }

        // 🔥 Authorization 헤더에서 JWT 토큰 추출
        String authHeader = request.getHeader("Authorization");

        // ✅ Authorization 헤더가 없거나, Bearer 토큰이 아닐 경우 필터 진행
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("⚠️ Authorization 헤더 없음 또는 Bearer 토큰 아님");
            chain.doFilter(request, response);
            return;
        }

        // 🔥 "Bearer " 제거 후 JWT 토큰만 추출
        String token = authHeader.substring(7);
        logger.debug("🔐 JWT 토큰 추출 완료");

        try {
            // 🔍 JWT에서 사용자 이름과 역할 정보 추출
            String username = jwtUtil.extractUsername(token);
            Set<String> roles = jwtUtil.extractRoles(token);
            logger.info("✅ 토큰 검증 성공 - 사용자: {}, 역할: {}", username, roles);

            // ✅ SecurityContextHolder에 인증 정보가 없는 경우에만 설정
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 🔍 UserDetailsService를 사용해 사용자 정보를 불러옴
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 🔥 역할(Set<String>)을 Spring Security에서 사용 가능한 GrantedAuthority 리스트로 변환
                List<GrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new) // "ROLE_USER" → SimpleGrantedAuthority("ROLE_USER")
                        .collect(Collectors.toList());

                // ✅ 인증 토큰 생성 (비밀번호 정보는 필요하지 않으므로 null)
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                // 🔍 요청의 세부 정보를 저장 (IP 주소 등)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // ✅ SecurityContext에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("✅ 인증 성공: SecurityContext에 저장됨");
            }
        } catch (ExpiredJwtException e) {
            // ❌ 토큰이 만료된 경우
            logger.warn("❌ JWT 만료 - {}", e.getMessage()); // 로그 기록
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"토큰이 만료되었습니다. 다시 로그인하세요.\"}"); // 보안상 간단한 메시지 제공
            return;
        } catch (JwtException e) {
            // ❌ 토큰이 유효하지 않은 경우
            logger.error("❌ JWT 검증 실패 - {}", e.getMessage()); // 로그 기록
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"유효하지 않은 토큰입니다.\"}"); // 보안상 간단한 메시지 제공
            return;
        }

        // ✅ 필터 체인 진행 (다음 필터로 요청 전달)
        chain.doFilter(request, response);
    }
}
