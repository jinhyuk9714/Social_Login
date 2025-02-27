package com.example.myblog.repository;

import com.example.myblog.entity.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(String userId); // ✅ userId로 삭제
}
