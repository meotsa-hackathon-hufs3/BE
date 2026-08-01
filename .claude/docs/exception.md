# 예외 처리 규약

에러 응답은 **`ErrorCode` 인터페이스 + `BusinessException` + `GlobalExceptionHandler`** 조합으로 일원화한다. 컨트롤러/서비스에서 임의 예외를 직접 던지지 않는다.

## 규칙

1. **도메인별 에러 코드**: `enum class XxxErrorCode : ErrorCode`로 정의하고 `status: HttpStatus`, `message: String`을 오버라이드한다.
   - 위치: `{domain}/exception/` (예: `AuthErrorCode`, `UserErrorCode`)
   - 여러 도메인이 공유하는 공통 에러는 `global/exception/`에 둔다.
2. **예외 발생**: 비즈니스 로직에서는 `throw BusinessException(XxxErrorCode.SOME_CASE)`.
3. **변환**: `GlobalExceptionHandler`(`@RestControllerAdvice`)가 잡아서 `ErrorResponse(message)` (JSON `{ "message": "..." }`)로 변환한다.

새 에러 상황이 생기면 해당 도메인의 `ErrorCode`에 케이스를 추가한 뒤 `BusinessException`으로 던진다.
