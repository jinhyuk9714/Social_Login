package com.example.myblog.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

/**
 * ✅ 회원가입 요청 DTO
 *
 * - 사용자가 회원가입할 때 필요한 정보를 담는 객체
 * - `@NotBlank`, `@Size`, `@Email` 등의 유효성 검사를 포함함
 */
@Getter  // ✅ Lombok - getter 자동 생성
@Setter  // ✅ Lombok - setter 자동 생성
public class SignupRequest {

    /**
     * ✅ 사용자 아이디 (username)
     *
     * - 최소 3자, 최대 20자 (`@Size`)
     * - 비어있으면 안됨 (`@NotBlank`)
     */
    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 3, max = 20, message = "아이디는 3자 이상 20자 이하로 입력해주세요.")
    private String username;

    /**
     * ✅ 비밀번호 (password)
     *
     * - 최소 6자, 최대 100자 (`@Size`)
     * - 비어있으면 안됨 (`@NotBlank`)
     */
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 6, max = 100, message = "비밀번호는 6자 이상 100자 이하로 입력해주세요.")
    private String password;

    /**
     * ✅ 이메일 (email)
     *
     * - 올바른 이메일 형식이어야 함 (`@Email`)
     * - 비어있으면 안됨 (`@NotBlank`)
     */
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    private String email;

    /**
     * ✅ 사용자 역할 (roles)
     *
     * - 선택 입력 가능 (예: "USER", "ADMIN")
     * - 기본값은 "USER"가 될 가능성이 높음
     */
    private Set<String> roles;
}
