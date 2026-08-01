# 빌드 · 실행 · 설정

## 실행 · 테스트

로컬 실행/테스트는 반드시 `local` 프로파일을 붙인다 (안 붙이면 DB 설정이 없어 실패).

```bash
SPRING_PROFILES_ACTIVE=local ./gradlew bootRun          # 로컬 실행 (MySQL localhost:3306 필요)
SPRING_PROFILES_ACTIVE=local ./gradlew test --daemon    # 테스트
./gradlew ktlintFormat                                  # 코드 포맷
```

## Git Hooks

`.githooks/`에 훅이 있고, 각각 commit, push 전에 동작한다.

- **pre-commit**: 스테이징된 `.kt`/`.kts`에 `ktlintFormat` 후 재-add
- **pre-push**: `local` 프로파일로 전체 테스트 실행

## 시크릿

- 운영값(`application.yml`)은 환경변수로 주입
- 로컬 값은 `application-local.yml`에 정의.
