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


public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 요청 URI 로그 남기기 (DEBUG로 변경)
        logger.debug("🔍 요청 URI: {}", request.getRequestURI());

        // ✅ OAuth2 로그인 관련 엔드포인트는 필터에서 제외
        if (request.getRequestURI().startsWith("/api/auth/oauth-success")) {
            logger.debug("⏩ OAuth2 로그인 요청 - 필터 패스");
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("⚠️ Authorization 헤더 없음 또는 Bearer 토큰 아님");
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        logger.debug("🔐 JWT 토큰 추출 완료");

        try {
            String username = jwtUtil.extractUsername(token);
            Set<String> roles = jwtUtil.extractRoles(token);
            logger.info("✅ 토큰 검증 성공 - 사용자: {}, 역할: {}", username, roles);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                List<GrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("✅ 인증 성공: SecurityContext에 저장됨");
            }
        } catch (ExpiredJwtException e) {
            logger.warn("❌ JWT 만료 - {}", e.getMessage()); // 로그에는 상세 메시지 남김
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"토큰이 만료되었습니다. 다시 로그인하세요.\"}"); // 보안상 일반적인 메시지 제공
            return;
        } catch (JwtException e) {
            logger.error("❌ JWT 검증 실패 - {}", e.getMessage()); // 로그에는 원인 남김
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"유효하지 않은 토큰입니다.\"}"); // 클라이언트에게 상세한 정보 제공 X
            return;
        }

        chain.doFilter(request, response);
    }
}
