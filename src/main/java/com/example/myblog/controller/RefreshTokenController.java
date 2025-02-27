package com.example.myblog.controller;

import com.example.myblog.entity.RefreshToken;
import com.example.myblog.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ✅ 리프레시 토큰 관리 컨트롤러
 *
 * - 리프레시 토큰을 생성하거나 삭제하는 API 제공
 * - 사용자는 userId 기반으로 토큰을 관리할 수 있음
 */
@RestController
@RequestMapping("/api/refresh-token")
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    /**
     * ✅ 생성자 주입
     *
     * @param refreshTokenService 리프레시 토큰을 관리하는 서비스
     */
    public RefreshTokenController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * ✅ 리프레시 토큰 생성
     *
     * @param userId 사용자의 ID
     * @return 생성된 RefreshToken 객체 반환
     */
    @PostMapping("/generate")
    public ResponseEntity<RefreshToken> generateToken(@RequestParam String userId) {
        RefreshToken token = refreshTokenService.createRefreshToken(userId);
        return ResponseEntity.ok(token);
    }

    /**
     * ✅ 리프레시 토큰 삭제
     *
     * @param userId 사용자의 ID
     * @return 200 OK 응답 반환
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteToken(@RequestParam String userId) {
        refreshTokenService.deleteByUserId(userId);
        return ResponseEntity.ok().build();
    }
}
