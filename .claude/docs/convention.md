# 코드 컨벤션

## 기본

- **언어**: Kotlin. 값 객체·DTO는 `data class`.
- **DI**: 생성자 주입만 사용 (`class Foo(private val bar: Bar)`). 필드 주입/`@Autowired` 금지.
- **파일 수정**: 전체 재작성 대신 필요한 줄만 타깃 수정.
- **코드 스타일**: ktlint 표준을 따르며, 포맷은 pre-commit(`ktlintFormat`)이 자동 처리한다. 빈 줄·들여쓰기 등은 수동으로 신경 쓰지 않는다 (예: 클래스 본문 맨 위/맨 아래 빈 줄은 ktlint가 제거).

## Controller

- `@RestController` + `@RequestMapping("/도메인")`.
- 응답은 `ResponseEntity<T>`로 반환하고 상태 코드를 명시한다.
- 요청 본문은 `@Valid @RequestBody`로 검증한다.
- 인증 주체는 `@AuthenticationPrincipal CustomUserDetails`로 접근한다.

## Service

- 클래스에 `@Transactional(readOnly = true)`를 기본으로 두고, **쓰기 메서드에만** `@Transactional`을 붙여 override 한다.

```kotlin
@Service
@Transactional(readOnly = true)
class AuthService(...) {
    @Transactional
    fun register(...) { ... }   // 쓰기
    fun findX(...) { ... }      // 읽기 → 클래스 기본(readOnly)
}
```

## Entity

- JPA `@Entity` + `@Table(name = "...")`.
- PK는 생성자가 아닌 **클래스 본문**에 `var id: Long? = null` + `@GeneratedValue(strategy = GenerationType.IDENTITY)`로 선언한다. 생성자에는 비즈니스상 필요한 필드만 둔다.
- enum 필드는 `@Enumerated(EnumType.STRING)`.

## DTO

- `data class`로 정의하고, 목적별로 `dto/request/`, `dto/response/`로 분리한다.
- 쿼리 결과 매핑은 `dto/projection/`에 둔다 (Kotlin `data class`).

## 예외 처리

에러 응답은 **`ErrorCode` 인터페이스 + `BusinessException` + `GlobalExceptionHandler`** 조합으로 일원화한다. 컨트롤러/서비스에서 임의 예외를 직접 던지지 않는다.

1. **도메인별 에러 코드**: `enum class XxxErrorCode : ErrorCode`로 정의하고 `status: HttpStatus`, `message: String`을 오버라이드한다.
   - 위치: `{domain}/exception/` (예: `AuthErrorCode`, `UserErrorCode`)
   - 여러 도메인이 공유하는 공통 에러는 `global/exception/`에 둔다.
2. **예외 발생**: 비즈니스 로직에서는 `throw BusinessException(XxxErrorCode.SOME_CASE)`.
3. **변환**: `GlobalExceptionHandler`(`@RestControllerAdvice`)가 잡아서 `ErrorResponse(message)` (JSON `{ "message": "..." }`)로 변환한다.

새 에러 상황이 생기면 해당 도메인의 `ErrorCode`에 케이스를 추가한 뒤 `BusinessException`으로 던진다.
