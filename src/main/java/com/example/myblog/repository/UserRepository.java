package com.example.myblog.repository;

import com.example.myblog.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    // ✅ 일반 로그인: 사용자명(username)으로 조회
    Optional<User> findByUsername(String username);

    // ✅ 소셜 로그인: 이메일(email)로 조회
    Optional<User> findByEmail(String email);

    // ✅ 일반 로그인: 사용자명 존재 여부 확인
    boolean existsByUsername(String username);

    // ✅ 이메일이 이미 등록되어 있는지 확인 (소셜 로그인 포함)
    boolean existsByEmail(String email);
}
