package com.example.myblog.repository;

import com.example.myblog.entity.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

/**
 * ✅ RefreshToken 저장소 (MongoDB)
 *
 * - MongoDB에 저장된 리프레시 토큰을 관리하는 Repository
 * - Spring Data MongoDB의 `MongoRepository`를 확장하여 기본적인 CRUD 기능 제공
 */
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    /**
     * ✅ 토큰 값으로 리프레시 토큰 조회
     *
     * - 로그인 후 **사용자가 새 AccessToken을 발급받을 때 사용됨**
     * - `Optional<RefreshToken>`을 반환하여, 토큰이 존재하지 않을 경우 `null` 대신 `Optional.empty()`를 반환
     *
     * @param token 리프레시 토큰 값
     * @return 해당 토큰을 포함하는 `Optional<RefreshToken>` 객체
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * ✅ 특정 사용자의 모든 리프레시 토큰 삭제
     *
     * - 사용자가 로그아웃할 때 **해당 userId의 모든 리프레시 토큰을 삭제**
     * - MongoDB에서는 `@Query` 없이도 메서드 명으로 자동으로 쿼리 생성 가능
     *
     * @param userId 사용자 ID (MongoDB의 `_id` 값)
     */
    void deleteByUserId(String userId);
}
