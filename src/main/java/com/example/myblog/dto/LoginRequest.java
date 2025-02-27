package com.example.myblog.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

/**
 * ✅ 로그인 요청 DTO
 *
 * - 사용자가 로그인할 때 필요한 정보를 담는 객체
 * - `@NotBlank` 어노테이션을 통해 유효성 검사 수행
 */
@Getter  // ✅ Lombok - getter 자동 생성
@Setter  // ✅ Lombok - setter 자동 생성
public class LoginRequest {

    /**
     * ✅ 사용자 아이디 (username)
     *
     * - 비어있으면 안됨 (`@NotBlank`)
     */
    @NotBlank(message = "아이디를 입력해주세요.")
    private String username;

    /**
     * ✅ 비밀번호 (password)
     *
     * - 비어있으면 안됨 (`@NotBlank`)
     */
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
