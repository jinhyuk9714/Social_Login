package com.example.myblog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")  // ✅ 모든 도메인 허용 (개발용)
public class HomeController {
    @GetMapping
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Welcome to the API");
    }
}
