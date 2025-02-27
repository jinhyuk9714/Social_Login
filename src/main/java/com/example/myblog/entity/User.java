package com.example.myblog.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.Set;
import java.util.Collections;

/**
 * ✅ MongoDB 사용자 엔티티 (User)
 *
 * - 일반 로그인 및 OAuth2 소셜 로그인 사용자 정보 저장
 * - `users` 컬렉션에서 관리됨
 */
@Getter  // ✅ Lombok - getter 자동 생성
@Setter  // ✅ Lombok - setter 자동 생성
@NoArgsConstructor // ✅ Lombok - 기본 생성자 자동 생성
@Document(collection = "users") // ✅ MongoDB 컬렉션 지정 (users)
public class User {

    /**
     * ✅ MongoDB에서 사용하는 ID 필드
     *
     * - `@Id` 어노테이션을 사용하여 MongoDB 문서의 기본 키 지정
     * - MongoDB의 기본 키 타입은 보통 String을 사용
     */
    @Id
    private String id;

    /**
     * ✅ 사용자명 (고유값)
     *
     * - 일반 로그인 시 username을 기반으로 인증 수행
     * - `@Field("username")`을 사용하여 MongoDB에서 저장될 필드명을 명시
     */
    @Field("username")
    private String username;

    /**
     * ✅ 비밀번호 (일반 로그인 사용자용)
     *
     * - 비밀번호는 **BCrypt 등의 암호화 방식으로 저장**해야 함
     */
    @Field("password")
    private String password;

    /**
     * ✅ 이메일 (고유값)
     *
     * - 소셜 로그인 사용자의 경우 username 대신 email을 기준으로 인증 가능
     */
    @Field("email")
    private String email;

    /**
     * ✅ 사용자 역할(Role)
     *
     * - 사용자의 권한 정보 저장 (`ROLE_USER`, `ROLE_ADMIN` 등)
     * - 여러 개의 역할을 가질 수 있도록 `Set<String>` 타입 사용
     */
    @Field("roles")
    private Set<String> roles;

    /**
     * ✅ OAuth2 로그인 제공자 (Google, GitHub 등)
     *
     * - 일반 로그인 사용자는 `null`
     * - OAuth 로그인 사용자는 `google`, `github` 등의 값 저장
     */
    @Field("oauth_provider")
    private String oauthProvider;

    /**
     * ✅ 프로필 이미지 URL
     *
     * - 소셜 로그인 사용자의 경우 제공된 프로필 이미지 URL 저장
     * - 일반 로그인 사용자는 직접 업로드 가능
     */
    @Field("profile_image")
    private String profileImage;

    /**
     * ✅ 새로운 OAuth 사용자 생성자
     *
     * - 이메일과 기본 역할을 지정하여 객체 생성
     * - 소셜 로그인 사용자의 경우 username이 없을 수 있으므로 email을 활용
     */
    public User(String email, String role) {
        this.email = email;
        this.roles = Collections.singleton(role);
    }
}
