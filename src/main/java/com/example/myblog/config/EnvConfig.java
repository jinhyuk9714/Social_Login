package com.example.myblog.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    public static String get(String key, String defaultValue) {
        return dotenv.get(key, defaultValue);  // 기본값 제공
    }

    public static String get(String key) {
        String value = dotenv.get(key);
        if (value == null) {
            throw new IllegalArgumentException("환경 변수 " + key + " 가 설정되지 않았습니다.");
        }
        return value;
    }
}
