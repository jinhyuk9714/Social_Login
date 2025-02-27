package com.example.myblog.repository;

import com.example.myblog.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

/**
 * ✅ User 저장소 (MongoDB)
 *
 * - MongoDB에서 `users` 컬렉션을 관리하는 Repository
 * - Spring Data MongoDB의 `MongoRepository`를 확장하여 기본적인 CRUD 기능 제공
 * - 사용자 조회 및 존재 여부 확인을 위한 메서드 포함
 */
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * ✅ 일반 로그인 사용자를 `username` 기준으로 조회
     *
     * - 사용자가 로그인할 때 `username`을 기반으로 `User` 객체를 찾음
     * - `Optional<User>`을 반환하여, 사용자가 존재하지 않으면 `Optional.empty()` 반환
     *
     * @param username 사용자명
     * @return 해당 username을 가진 `Optional<User>` 객체
     */
    Optional<User> findByUsername(String username);

    /**
     * ✅ 소셜 로그인 사용자를 `email` 기준으로 조회
     *
     * - Google, Facebook 등 OAuth2를 이용한 사용자의 경우 `username` 대신 `email`로 조회 필요
     * - `Optional<User>`을 반환하여, 사용자가 존재하지 않으면 `Optional.empty()` 반환
     *
     * @param email 사용자 이메일
     * @return 해당 email을 가진 `Optional<User>` 객체
     */
    Optional<User> findByEmail(String email);

    /**
     * ✅ 사용자명(username)이 이미 존재하는지 확인
     *
     * - 회원가입 시 `username`이 중복되는지 확인하기 위해 사용됨
     * - 존재하면 `true`, 없으면 `false` 반환
     *
     * @param username 중복 확인할 사용자명
     * @return 존재 여부 (`true`: 이미 존재, `false`: 사용 가능)
     */
    boolean existsByUsername(String username);

    /**
     * ✅ 이메일(email)이 이미 등록되어 있는지 확인 (소셜 로그인 포함)
     *
     * - 회원가입 또는 OAuth2 소셜 로그인 시 `email`이 이미 등록된 경우 중복 방지
     * - 존재하면 `true`, 없으면 `false` 반환
     *
     * @param email 중복 확인할 이메일 주소
     * @return 존재 여부 (`true`: 이미 존재, `false`: 사용 가능)
     */
    boolean existsByEmail(String email);
}
