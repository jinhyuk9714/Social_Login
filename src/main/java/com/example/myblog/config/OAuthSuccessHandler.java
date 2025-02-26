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

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {
    private final OAuth2UserService oAuth2UserService;

    public OAuthSuccessHandler(OAuth2UserService oAuth2UserService) {
        this.oAuth2UserService = oAuth2UserService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        System.out.println("✅ OAuth 로그인 성공!"); // ✅ 로그 확인

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // ✅ OAuth2User 정보를 기반으로 사용자 저장 + JWT 발급
        TokenResponse tokenResponse = oAuth2UserService.processOAuthUser(oAuth2User);

        System.out.println("✅ JWT 발급 완료! Access Token: " + tokenResponse.getAccessToken());

        // ✅ JWT를 프론트엔드로 전달 (URL 인코딩 적용)
        String redirectUrl = "http://localhost:8080/api/auth/oauth-success"
                + "?accessToken=" + URLEncoder.encode(tokenResponse.getAccessToken(), StandardCharsets.UTF_8)
                + "&refreshToken=" + URLEncoder.encode(tokenResponse.getRefreshToken(), StandardCharsets.UTF_8);

        response.sendRedirect(redirectUrl);
    }
}
