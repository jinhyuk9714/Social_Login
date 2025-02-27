package com.example.myblog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * ✅ JWT 토큰 응답 DTO
 *
 * - 로그인 성공 또는 OAuth2 인증 후 발급되는 토큰 정보 저장
 * - 클라이언트에게 `accessToken`과 `refreshToken`을 반환할 때 사용됨
 */
@Getter  // ✅ Lombok - getter 자동 생성
@Setter  // ✅ Lombok - setter 자동 생성
@AllArgsConstructor  // ✅ Lombok - 모든 필드를 포함한 생성자 자동 생성
public class TokenResponse {

    /**
     * ✅ 액세스 토큰 (Access Token)
     *
     * - 유효 기간이 짧음 (예: 15분)
     * - API 요청 시 사용됨 (Authorization 헤더)
     */
    private String accessToken;

    /**
     * ✅ 리프레시 토큰 (Refresh Token)
     *
     * - 유효 기간이 김 (예: 7일)
     * - 액세스 토큰이 만료되면 새 액세스 토큰을 발급받을 때 사용됨
     */
    private String refreshToken;
}
