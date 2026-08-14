# PR 컨벤션

커밋 컨벤션을 준수한다.

## PR 제목

```
<type>(<scope>): <subject>
```

**`<type>`**

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

**`<subject>`**

- 한글로 작성
- 명사형 종결 (예: `~ 추가`)
- 50자 이내로 간결하게

## PR 본문

아래 템플릿의 섹션 구성과 각 섹션 주석의 작성 지침을 따른다.

@.github/PULL_REQUEST_TEMPLATE.md
