package com.example.myblog.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Getter
@Setter
@Document(collection = "refresh_tokens") // ✅ MongoDB 컬렉션 지정
public class RefreshToken {

    @Id
    private String id; // ✅ MongoDB에서는 ID를 String 타입으로 설정

    private String token;

    @DBRef // ✅ MongoDB에서 관계형 데이터를 참조할 때 사용
    private User user;
}
