package com.example.myblog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ✅ 홈 컨트롤러
 *
 * - API의 기본 엔드포인트 (`/`)를 처리
 * - CORS 허용 (`*`)으로 모든 도메인에서의 요청을 허용 (개발용)
 */
@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")  // ✅ 모든 도메인 허용 (프론트엔드 개발 시 편의를 위해 설정)
public class HomeController {

    /**
     * ✅ 기본 경로 (`/`) 요청을 처리
     *
     * @return "Welcome to the API" 메시지 반환
     */
    @GetMapping
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Welcome to the API");
    }
}
