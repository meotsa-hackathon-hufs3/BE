# 아키텍처

## 패키지 구조

`com.meotsa` 하위를 **도메인(domain)별**로 나누고, 공통은 `global/`에 둔다.

```
com.meotsa/
├── global/
│   ├── config/         # 설정 클래스
│   ├── docs/           # 공통 Swagger 애노테이션
│   ├── exception/      # 전역 예외 처리
│   ├── jwt/            # JWT 토큰 발급·검증
│   └── security/       # Spring Security 구성 요소
│
├── {domain}/
│   ├── controller/
│   ├── docs/           # Swagger 스펙 인터페이스
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── dto/
│   └── exception/
│
└── MeotsaHackathonApplication.kt
```

- 새 도메인을 추가할 때는 `com.meotsa.<domain>/` 아래에 위 레이어(controller/docs/service/repository/entity/dto/exception)를 동일하게 구성한다.
- 도메인별 예외는 `{domain}/exception/`에, 전역 공통 예외는 `global/exception/`에 둔다.
- Swagger 스펙은 `{domain}/docs/`에 `XxxSwaggerSpec` 인터페이스로 두고 컨트롤러가 구현한다. 여러 도메인이 공유하는 응답 애노테이션은 `global/docs/`에 둔다.
