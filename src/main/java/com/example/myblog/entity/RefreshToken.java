package com.example.myblog.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

/**
 * ✅ MongoDB 리프레시 토큰 엔티티
 *
 * - 로그인 후 발급된 리프레시 토큰을 저장
 * - 액세스 토큰 만료 시, 해당 토큰을 이용해 새 액세스 토큰 발급 가능
 */
@Getter  // ✅ Lombok - getter 자동 생성
@Setter  // ✅ Lombok - setter 자동 생성
@Document(collection = "refresh_tokens") // ✅ MongoDB 컬렉션 지정
public class RefreshToken {

    /**
     * ✅ MongoDB에서 사용하는 ID 필드
     *
     * - `@Id` 어노테이션을 사용하여 MongoDB 문서의 기본 키 지정
     * - MongoDB의 기본 키 타입은 보통 String을 사용
     */
    @Id
    private String id;

    /**
     * ✅ 저장된 리프레시 토큰 값
     *
     * - 사용자가 새 액세스 토큰을 요청할 때, 해당 토큰을 검증하여 새 토큰을 발급
     */
    private String token;

    /**
     * ✅ 리프레시 토큰 만료 시간
     *
     * - `Instant` 타입을 사용하여 만료 시간을 저장 (UTC 표준 시간)
     * - 리프레시 토큰이 만료되었는지 검증할 때 사용
     */
    private Instant expiryDate;

    /**
     * ✅ 리프레시 토큰과 연결된 사용자 ID
     *
     * - `@DBRef` 대신, **사용자의 ID만 저장**하여 성능 최적화
     * - 필요하면 `UserRepository`를 통해 사용자 정보 조회 가능
     */
    private String userId;
}
