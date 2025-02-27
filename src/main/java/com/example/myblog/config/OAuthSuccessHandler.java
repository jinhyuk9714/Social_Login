package com.example.myblog.config;

import com.example.myblog.dto.TokenResponse;
import com.example.myblog.service.OAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * ✅ OAuthSuccessHandler
 *
 * - OAuth2 인증 성공 후 실행되는 핸들러
 * - OAuth2User 정보를 기반으로 사용자 저장 및 JWT 발급
 * - JWT를 클라이언트에게 전달하기 위해 URL에 포함하여 리디렉트 처리
 */
@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {
    private final OAuth2UserService oAuth2UserService;

    /**
     * ✅ OAuthSuccessHandler 생성자
     *
     * @param oAuth2UserService OAuth2 로그인 후 사용자 저장 및 JWT 발급을 담당하는 서비스
     */
    public OAuthSuccessHandler(OAuth2UserService oAuth2UserService) {
        this.oAuth2UserService = oAuth2UserService;
    }

    /**
     * ✅ OAuth2 로그인 성공 시 호출됨
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param authentication 인증된 사용자의 정보를 포함하는 객체
     * @throws IOException 리디렉트 처리 중 발생할 수 있는 예외
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        System.out.println("✅ OAuth 로그인 성공!"); // ✅ 로그 확인

        // 🔥 OAuth2 로그인한 사용자 정보 가져오기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 🔥 OAuth2User 정보를 기반으로 사용자 저장 + JWT 발급
        TokenResponse tokenResponse = oAuth2UserService.processOAuthUser(oAuth2User);

        System.out.println("✅ JWT 발급 완료! Access Token: " + tokenResponse.getAccessToken());

        // 🔥 프론트엔드에 JWT 전달을 위한 URL 생성 (URL 인코딩 적용)
        String redirectUrl = "http://localhost:8080/api/auth/oauth-success"
                + "?accessToken=" + URLEncoder.encode(tokenResponse.getAccessToken(), StandardCharsets.UTF_8)
                + "&refreshToken=" + URLEncoder.encode(tokenResponse.getRefreshToken(), StandardCharsets.UTF_8);

        // ✅ 프론트엔드로 리디렉트
        response.sendRedirect(redirectUrl);
    }
}
