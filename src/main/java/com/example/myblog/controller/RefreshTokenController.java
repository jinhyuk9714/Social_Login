package com.example.myblog.controller;

import com.example.myblog.entity.RefreshToken;
import com.example.myblog.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/refresh-token")
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    public RefreshTokenController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/generate")
    public ResponseEntity<RefreshToken> generateToken(@RequestParam String userId) { // ✅ userId 기반
        RefreshToken token = refreshTokenService.createRefreshToken(userId);
        return ResponseEntity.ok(token);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteToken(@RequestParam String userId) { // ✅ userId로 삭제
        refreshTokenService.deleteByUserId(userId);
        return ResponseEntity.ok().build();
    }
}
