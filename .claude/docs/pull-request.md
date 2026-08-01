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

`.github/PULL_REQUEST_TEMPLATE.md` 템플릿을 따른다 (개요 / 변경 사항 / 참고).

**참고(이슈 연결)**

- `Refs #(이슈 번호)` — 이슈 작업 중 부분 PR
- `Closes #(이슈 번호)` — 해당 PR로 이슈 해결 시
- 없을 경우 생략
