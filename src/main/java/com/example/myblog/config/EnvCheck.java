package com.example.myblog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class EnvCheck {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @PostConstruct
    public void init() {
        System.out.println("🔍 GOOGLE_CLIENT_ID: " + googleClientId);
        System.out.println("🔍 GOOGLE_CLIENT_SECRET: " + googleClientSecret);
    }
}
