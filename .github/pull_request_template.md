## 결과

<!-- PR을 열자마자 결과가 보이게 작성합니다. 구현 과정은 Issue에 남기고 여기서는 결과를 먼저 보여줍니다. -->

**한 줄 결과:** `Before → After`

- Closes #
- 운영 모드: `Personal Refactoring Mode | V3 Sprint Mode`
- 외부 동작 변경: `없음 | 있음`

## Before / After

<!-- 코드 변경 PR이면 실제 Diff에서 대표 코드 1~3개를 가져옵니다. 설명만 쓰지 않습니다. 파일 경로도 적습니다. -->

### 대표 변경 1

파일: `path/to/File.java`

**Before**

```java
// 실제 변경 전 코드
```

**After**

```java
// 실제 변경 후 코드
```

**달라진 점:**

-

### 대표 변경 2

<!-- 필요할 때만 작성합니다. 코드 변경이 없으면 N/A와 이유를 적습니다. -->

**Before**

```java

```

**After**

```java

```

**달라진 점:**

-

### 전체 결과 요약

| 항목 | Before | After |
|---|---|---|
| 핵심 현상 또는 구조 |  |  |
| 수치가 의미 있는 경우 |  |  |

## 왜 바꿨나

<!-- 문제와 판단만 짧게 적습니다. 구현 과정의 세부 로그는 Issue에 남깁니다. -->

- 기존 문제:
- 이 변경으로 얻은 것:
- 그대로 유지한 것:

## 검증

| 항목 | 결과 | 근거 |
|---|---|---|
| 관련 테스트 | `PASS | FAIL | N/A` |  |
| 전체 테스트 | `PASS | FAIL | N/A` |  |
| 전체 build | `PASS | FAIL | N/A` |  |
| 핵심 동작 확인 | `PASS | FAIL | N/A` |  |
| SonarQube | `PASS | FAIL | NOT_RUN | N/A` |  |
| JaCoCo | `PASS | FAIL | NOT_RUN | N/A` |  |

## AI 활용과 최종 판단

- AI가 한 일:
- Human이 결정한 일:
- AI Self Review: `MERGEABLE | BLOCK | 미실행`
- BLOCKER:
- MAJOR:

## 면접용 Q&A

<!-- AI가 이 PR의 실제 결과를 기준으로 쉬운 질문 2~3개와 답안을 함께 작성합니다. 추상적인 고정 질문은 쓰지 않습니다. -->

### Q1. [이번 결과를 가장 쉽게 설명하는 질문]

**답:**

-

### Q2. [왜 이렇게 판단했는지 묻는 질문]

**답:**

-

### Q3. [검증 또는 핵심 개념을 묻는 질문, 필요할 때만]

**답:**

-

### Human 확인

- [ ] 위 Before / After가 실제 Diff와 일치한다.
- [ ] 위 Q&A가 실제 변경과 일치한다.
- [ ] 코드 없이도 결과와 이유를 설명할 수 있다.
- [ ] 이해되지 않는 AI 생성 핵심 코드가 없다.

별도 장문 답변은 작성하지 않습니다. 틀린 내용이나 이해가 안 되는 부분만 수정하고 확인합니다.

## Output

- Troubleshooting 기록: `YES | NO`
- Tech Blog: `YES | NO`
- Interview Note: `YES | NO`
- Portfolio Candidate: `YES | NO`

PR 자체는 항상 `결과 → Before / After → 이유 → 검증` 순서의 트러블슈팅형 구조로 작성합니다. 별도 Troubleshooting 문서는 문제 재현과 원인 분석 가치가 충분할 때만 만듭니다.

## Merge Gate

- [ ] Issue 범위 밖 변경 없음
- [ ] 실제 코드 Before / After 확인 완료
- [ ] 필요한 테스트와 build 검증 완료
- [ ] 필요한 수치 또는 구조 Before / After 확인 완료
- [ ] 최신 Head AI Self Review 완료
- [ ] 미해결 BLOCKER 없음
- [ ] 미해결 MAJOR 없음
- [ ] Human 확인 완료
- [ ] Output 판정 완료

최종 Merge는 Human이 수행합니다.
