package com.example.myblog.repository;

import com.example.myblog.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    // ✅ 사용자명이 존재하는지 확인하는 메서드 추가
    boolean existsByUsername(String username);
}
