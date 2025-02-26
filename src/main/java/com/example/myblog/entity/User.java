package com.example.myblog.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.Set;
import java.util.Collections;

@Getter
@Setter
@NoArgsConstructor // ✅ 기본 생성자 추가
@Document(collection = "users") // ✅ MongoDB에서 users 컬렉션 사용
public class User {

    @Id
    private String id;

    @Field("username")
    private String username;

    @Field("password")
    private String password;

    @Field("email")
    private String email;

    @Field("roles")
    private Set<String> roles;

    @Field("oauth_provider")
    private String oauthProvider;

    @Field("profile_image")
    private String profileImage;

    // ✅ 새로운 생성자 추가
    public User(String email, String role) {
        this.email = email;
        this.roles = Collections.singleton(role);
    }
}
