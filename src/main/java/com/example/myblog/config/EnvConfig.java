package com.example.myblog.config;

import io.github.cdimascio.dotenv.Dotenv;  // Dotenv 라이브러리 사용
import org.springframework.context.annotation.Configuration;

/**
 * ✅ EnvConfig 클래스
 *
 * 이 클래스는 환경 변수를 로드하고, 이를 쉽게 가져올 수 있도록 도와주는 역할을 합니다.
 *
 * - `.env` 파일을 사용하여 환경 변수를 관리 (로컬 개발 환경에서 주로 사용)
 * - 환경 변수가 존재하지 않을 경우 예외를 던질 수도 있음
 * - Spring의 `@Configuration`을 붙여서 애플리케이션이 설정 클래스로 인식하도록 함
 */
@Configuration
public class EnvConfig {

    // ✅ Dotenv 객체를 초기화하여 .env 파일에서 환경 변수를 로드
    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing() // .env 파일이 없어도 예외 발생하지 않도록 설정
            .load(); // 환경 변수 로드

    /**
     * ✅ 환경 변수 값을 가져오는 메서드 (기본값 제공)
     *
     * @param key 환경 변수 키
     * @param defaultValue 환경 변수가 없을 경우 사용할 기본값
     * @return 환경 변수 값 또는 기본값
     */
    public static String get(String key, String defaultValue) {
        return dotenv.get(key, defaultValue);  // 환경 변수가 없으면 기본값 반환
    }

    /**
     * ✅ 환경 변수 값을 가져오는 메서드 (예외 발생)
     *
     * - 환경 변수가 존재하지 않으면 예외를 던짐
     * - 필수 환경 변수일 경우 이 메서드를 사용해야 함
     *
     * @param key 환경 변수 키
     * @return 환경 변수 값
     * @throws IllegalArgumentException 환경 변수가 존재하지 않으면 예외 발생
     */
    public static String get(String key) {
        String value = dotenv.get(key);

        if (value == null) {
            throw new IllegalArgumentException("환경 변수 " + key + " 가 설정되지 않았습니다."); // ❌ 필수 값이 없으면 예외 발생
        }

        return value;
    }
}
