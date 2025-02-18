package com.example.myblog.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

@Getter
@Setter
@Document(collection = "users") // ✅ MongoDB 전용 @Document 어노테이션 사용
public class User {

    @Id
    private String id; // ✅ MongoDB에서는 String 타입 ID 사용

    @Field("username") // ✅ MongoDB 필드 매핑
    private String username;

    @Field("password")
    private String password;

    @Field("email")
    private String email;

    @Field("roles")
    private Set<String> roles;
}
