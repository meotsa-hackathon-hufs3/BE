# 커밋 컨벤션

```
<type>(<scope>): <subject>
```

예시: `feat(auth): 카카오 소셜 로그인 추가`

## `<type>`

- `feat` — feature
- `fix` — bug fix
- `docs` — documentation
- `style` — formatting, missing semi colons 등
- `refactor`
- `test` — 누락된 테스트 추가
- `chore` — maintain
- `ci` — CI/CD 설정 변경

## `<scope>` (생략 가능)

- 도메인 기준

## 주요 변경 강조

- `!`를 사용해 브레이킹 변경 또는 중요한 변경을 강조한다.
- 예시:
    - `feat!: 회원가입 인증 메일에 고양이 사진을 미포함`
    - `feat(auth)!: 회원가입 인증 메일에 고양이 사진을 미포함`

---

`subject`는 한글로 작성하고, `Co-Authored-By` 트레일러는 넣지 않는다.

본문 없이 **제목 한 줄로만** 작성한다.
