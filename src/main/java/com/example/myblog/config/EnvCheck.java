package com.example.myblog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

/**
 * ✅ EnvCheck 클래스
 *
 * 이 클래스는 Spring Boot의 환경 변수 중 Google OAuth2 인증 관련 설정 값을 가져와서
 * 애플리케이션이 시작될 때 콘솔에 출력하는 역할을 합니다.
 *
 * - @Component: 스프링 빈으로 등록하여 애플리케이션 실행 시 자동으로 동작하게 함
 * - @Value: application.properties(yml)에서 설정된 값을 주입받음
 * - @PostConstruct: 빈이 생성된 후 자동으로 실행되는 메서드를 지정
 */
@Component
public class EnvCheck {

    // ✅ application.properties (또는 yml)에서 Google OAuth2 클라이언트 ID 값을 가져옴
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    // ✅ Google OAuth2 클라이언트 Secret 값을 가져옴
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    /**
     * ✅ 애플리케이션이 실행되면 Google OAuth2 설정값을 출력하는 메서드
     *
     * @PostConstruct 어노테이션이 붙은 메서드는 객체가 생성되고 DI가 완료된 후 자동으로 실행됨.
     * 즉, 애플리케이션이 시작될 때 한 번 실행됨.
     *
     * - OAuth2 설정 값이 정상적으로 로드되었는지 확인하는 용도
     * - 실제 운영 환경에서는 보안상 client-secret 출력은 피해야 함
     */
    @PostConstruct
    public void init() {
        System.out.println("🔍 GOOGLE_CLIENT_ID: " + googleClientId); // Google Client ID 출력
        System.out.println("🔍 GOOGLE_CLIENT_SECRET: " + googleClientSecret); // Google Client Secret 출력 (운영환경에서는 제거해야 함!)
    }
}
