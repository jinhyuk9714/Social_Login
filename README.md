# 🔐 Social Auth & JWT Backup Repository

이 저장소는 **Spring Boot 기반의 인증 및 보안 기능을 백업 및 관리**하기 위한 저장소입니다.  
OAuth2 로그인, JWT 인증, Refresh Token, Redis 세션 관리 등을 포함합니다.

> 📝 **이 저장소는 블로그 프로젝트의 인증 관련 기능을 백업하기 위한 용도로 사용됩니다.**  
> 블로그 프로젝트의 전체 코드 및 게시글 관련 기능은 [MyBlog Repository](https://github.com/jinhyuk9714/myblog)에서 확인하세요.

---

## 📌 프로젝트 개요

이 프로젝트는 **Spring Security + OAuth2 + JWT + Redis**를 활용하여  
안전한 인증 및 보안 기능을 제공하는 백엔드 시스템을 구성합니다.

### ✅ 포함된 주요 기능
- **사용자 인증**
  - ✅ 일반 로그인 (JWT 기반)
  - ✅ Google OAuth2 소셜 로그인
  - ✅ JWT + Redis 기반 리프레시 토큰 관리
- **보안 기능**
  - ✅ JWT 토큰 검증 및 만료 처리
  - ✅ Redis를 활용한 Refresh Token 저장
  - ✅ OAuth2 인증 후 JWT 발급 및 사용자 저장

---

## 📂 프로젝트 폴더 구조

이 프로젝트는 **Spring Boot 백엔드 코드**로 구성되어 있습니다.

```
📦 social-auth-backup
 ┣ 📂 src/
 ┃ ┣ 📂 main/java/com/example/auth
 ┃ ┃ ┣ 📂 config/         # ✅ 보안 설정 (JWT, OAuth2, Redis 등)
 ┃ ┃ ┣ 📂 controller/     # ✅ 인증 관련 API 컨트롤러
 ┃ ┃ ┣ 📂 dto/            # ✅ 데이터 전송 객체 (Request, Response)
 ┃ ┃ ┣ 📂 entity/         # ✅ MongoDB 엔티티 (User, RefreshToken 등)
 ┃ ┃ ┣ 📂 repository/     # ✅ MongoDB 저장소 (User, RefreshToken)
 ┃ ┃ ┣ 📂 service/        # ✅ 비즈니스 로직 (인증, 토큰 관리)
 ┃ ┃ ┗ 📂 utils/          # ✅ JWT, OAuth 유틸리티 클래스
 ┣ 📄 pom.xml             # ✅ 프로젝트 의존성 관리 (Maven)
 ┣ 📄 README.md           # ✅ 현재 파일
 ┣ 📄 .gitignore          # ✅ Git 추적 제외 파일 목록
 ┣ 📄 application.yml     # ✅ 환경 설정 파일 (DB, OAuth, JWT 키 등)
 ┗ 📄 Dockerfile          # ✅ Docker 배포 설정 파일 (선택사항)
```

---

## 🚀 기술 스택

### 🔹 Backend
- **Spring Boot 3.0**
- **Spring Security & OAuth2 Client** (JWT 인증 & 소셜 로그인)
- **MongoDB** (NoSQL 데이터베이스)
- **Redis** (세션 관리 & Refresh Token 저장)
- **Lombok** (코드 간결화)

---

## 🔑 API 사용 방법

### 🔹 1️⃣ 로컬 실행 방법

```bash
# 1. 저장소 클론
git clone https://github.com/jinhyuk9714/Social_Login.git
cd social-auth-backup

# 2. 환경 변수 설정 (application.yml 필요)
cp src/main/resources/application.yml
# 필요한 정보 (DB 연결, OAuth 키) 수정

# 3. 빌드 & 실행
./mvnw clean package
java -jar target/auth-backup-0.0.1-SNAPSHOT.jar
```

---

## 🔐 주요 기능 설명

### 🏷️ 1. 사용자 인증 (로그인 & 회원가입)
- 🔐 **JWT 기반 로그인** → `POST /api/auth/login`
- 🔐 **Google OAuth2 소셜 로그인** → `GET /api/auth/oauth-success`
- 🔐 **로그아웃 (Redis에서 리프레시 토큰 삭제)** → `POST /api/auth/logout`
- 🔐 **JWT 기반 사용자 정보 조회** → `GET /api/auth/user`

### 🔄 2. Refresh Token 관리
- ♻️ **Access Token 갱신** → `POST /api/auth/refresh`
- ❌ **Refresh Token 삭제 (로그아웃 시)** → `POST /api/auth/logout`

### 🔑 3. JWT 검증 및 보안 기능
- 🔍 **JWT 토큰 검증** → `JwtUtil.validateToken(token)`
- 📅 **토큰 만료 시간 체크** → `JwtUtil.extractExpiration(token)`
- 🔑 **JWT에서 사용자 정보 추출** → `JwtUtil.extractUsername(token)`

---

## 🛠 환경 변수 설정 (`application.yml` 예시)

```yaml
spring:
  config:
    import: optional:file:.env  # ✅ .env 파일에서 환경 변수 로드
  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb://localhost:27017/blog}  # ✅ 환경 변수 사용
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
  cache:
    type: redis  # ✅ Redis를 기본 캐시로 설정
  jackson:
    time-zone: UTC  # ✅ JSON 직렬화 시 UTC 사용
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope:
              - profile
              - email
            redirect-uri: "{baseUrl}/login/oauth2/code/google"

server:
  port: 8080

jwt:
  secret-key: ${JWT_SECRET_KEY}
  access-token-expiration: ${ACCESS_TOKEN_EXPIRATION:900000}  # ✅ 15분 (밀리초)
  refresh-token-expiration: ${REFRESH_TOKEN_EXPIRATION:604800000}  # ✅ 7일 (밀리초)

logging:
  level:
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
    com.example.myblog: DEBUG

```

---
