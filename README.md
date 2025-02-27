# 🔐 Social Auth & JWT Authentication Backup

이 저장소는 **Spring Security 기반의 인증 시스템**을 백업하는 저장소입니다.  
원본 프로젝트는 유지하면서, **소셜 로그인, JWT 인증, 리프레시 토큰, Redis 세션 관리 기능**을 따로 관리합니다.

> 🚀 **주의:** 이 저장소는 **블로그 기능을 포함하지 않습니다.**  
> 원본 프로젝트는 [여기](https://github.com/your-username/original-repo)에서 확인하세요.

---

## 📌 프로젝트 개요

이 저장소는 **Spring Security + OAuth2 + JWT + Redis 기반 인증 시스템**을 보관합니다.  
소셜 로그인과 JWT 기반 인증이 필요한 프로젝트에서 활용할 수 있습니다.

### ✅ 포함된 주요 기능
- **소셜 로그인 (OAuth2)**
  - Google OAuth2 로그인을 지원 (Spring Security + OAuth2 Client)
  - 소셜 로그인 후 **JWT 토큰 발급** 및 사용자 데이터 저장
- **JWT 기반 인증 & 인가**
  - Access Token & Refresh Token 발급 및 검증
  - 만료된 액세스 토큰을 리프레시 토큰으로 갱신
- **Redis 기반 세션 관리**
  - Refresh Token을 Redis에 저장하여 보안 강화
  - 로그아웃 시 Redis에서 해당 토큰 제거
- **Spring Security 커스터마이징**
  - `JwtFilter`를 활용한 요청 필터링
  - 사용자 인증 & 권한 부여 (`ROLE_USER`, `ROLE_ADMIN` 등)

---
