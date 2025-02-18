package com.example.myblog.repository;

import com.example.myblog.entity.RefreshToken;
import com.example.myblog.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {  // ✅ Long → String 변경
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
    void deleteByUser(User user);
}
