package com.example.myblog.repository;

import com.example.myblog.entity.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserId(String userId); // ✅ userId로 조회
    void deleteByUserId(String userId); // ✅ userId로 삭제
}
