package com.example.myblog.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Getter
@Setter
@Document(collection = "refresh_tokens") // ✅ MongoDB 컬렉션 지정
public class RefreshToken {

    @Id
    private String id; // ✅ MongoDB에서는 String 타입 ID 사용

    private String token;

    private Instant expiryDate; // ✅ 만료 시간 필드 추가

    private String userId; // ✅ `@DBRef` 제거하고, User의 ID만 저장
}
