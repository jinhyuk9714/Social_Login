package com.example.myblog.service;

import com.example.myblog.entity.RefreshToken;
import com.example.myblog.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * ✅ RefreshTokenService 생성자
     * - RefreshTokenRepository를 주입받아 DB 연동
     */
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * ✅ 새로운 Refresh Token 생성
     * - UUID를 이용해 고유한 토큰을 생성
     * - 현재 시간 + 1시간(3600초) 후 만료 설정
     * - userId와 함께 DB에 저장 후 반환
     */
    public RefreshToken createRefreshToken(String userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString()); // 🔥 랜덤한 UUID 기반 토큰 생성
        refreshToken.setExpiryDate(Instant.now().plusSeconds(3600)); // 🔥 1시간 후 만료 설정
        refreshToken.setUserId(userId);

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * ✅ Refresh Token을 기반으로 데이터 조회
     * - 특정 토큰이 DB에 존재하는지 확인
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * ✅ 특정 사용자의 Refresh Token 삭제
     * - 사용자가 로그아웃하면 해당 사용자의 Refresh Token을 삭제하여 세션 종료
     */
    public void deleteByUserId(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
