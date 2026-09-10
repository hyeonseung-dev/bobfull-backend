## 한 줄 요약

무엇을:
왜:
기술부채(있다면):
의도부채(있다면, Issue 논의와 달라진 부분):

## 관련 Issue

- Closes #
- 검토 수준: `기본 | 강화`
- 운영 모드: `Personal Refactoring Mode | V3 Sprint Mode`

<!--
개인 리팩토링 PR은 docs/PERSONAL_REFACTORING_WORKFLOW.md와 skills/bobfull-refactoring/SKILL.md를 따릅니다.
개인 리팩토링에서는 AI가 작성한 코드라도 Human이 설명할 수 없으면 Merge하지 않습니다.
-->

## PR 이해 요약

### 쉬운 설명

<!-- 처음 보는 사람이 3~5문장으로 무엇을 왜 바꿨는지 이해할 수 있게 작성합니다. -->

-

### 주요 실행 흐름

1.
2.
3.

### Mermaid 시각화

<!-- 의미 있는 실행 흐름 또는 구조 변화가 있을 때만 작성합니다. 단순 문서·설정·CRUD면 `해당 없음`과 이유를 작성합니다. -->

해당 없음:

### 주요 개념

| 개념 | 쉽게 말하면 | 이 PR에서 왜 필요한가 |
|---|---|---|
|  |  |  |

### 코드 읽는 순서

1.
2.
3.

## Why

### 문제

-

### 왜 문제인가

- 유지보수성:
- 도메인 책임:
- 테스트 난이도:
- 변경 영향 범위:

## Before

### 기존 구조

```text
기존 실행 흐름 또는 책임 구조
```

### Baseline

- 관련 테스트:
- 전체 build:
- SonarQube:
- JaCoCo:
- 기타 측정·재현 결과:

## What

### 주요 변경

-

### 변경하지 않은 계약

- API:
- DB Schema:
- 비즈니스 정책:
- 상태 전이:

## Why this approach

### 검토한 방법

| 후보 | 장점 | 단점 | 판단 |
|---|---|---|---|
| A |  |  |  |
| B |  |  |  |

### 최종 선택

- 선택한 방법:
- 선택 이유:
- 선택하지 않은 방법과 이유:
- 주요 trade-off:

## Verification

### 추가·수정 테스트

| 테스트 클래스·파일 | 검증 시나리오 | 이 테스트가 보장하는 것 |
|---|---|---|
|  |  |  |

### 완료 조건·검증 결과

| Issue 완료 조건 | 구현 위치 | 검증 증거 | 결과 |
|---|---|---|---|
|  |  |  | `PASS | FAIL | NOT_RUN` |

### 필수 검증

| Merge Gate | 실행 명령·환경 | 결과 | 증거·한계 |
|---|---|---|---|
| 관련 테스트 |  | `PASS | FAIL | NOT_RUN` |  |
| 전체 build |  | `PASS | FAIL | NOT_RUN` |  |
| 핵심 기능 직접 검증 | Postman/curl/직접 트리거 등 | `PASS | FAIL | NOT_RUN` |  |
| SonarQube | 필요한 경우 | `PASS | FAIL | NOT_APPLICABLE` |  |
| JaCoCo | 필요한 경우 | `PASS | FAIL | NOT_APPLICABLE` |  |
| Before/After Evidence | `docs/evidence/...` 또는 N/A 근거 | `PASS | FAIL | NOT_APPLICABLE` |  |
| 담당 구현 AI Review | PR Conversation 댓글 | `MERGEABLE | BLOCK | 미실행` |  |

- 최신 검증 Commit SHA:
- 미해결 BLOCKER:
- 미해결 MAJOR:
- Human 결정 필요 사항:

## Before / After Evidence

<!--
성능·신뢰성·동시성·인프라·캐시·Kafka/Outbox·AI뿐 아니라 개인 리팩토링의 구조 개선 근거도 작성합니다.
정량 KPI가 의미 없으면 NOT_APPLICABLE 이유와 대신 사용한 구조·테스트·복잡도·의존성 근거를 작성합니다.
실제 측정 전 임의 수치를 작성하지 않습니다.
-->

- Evidence 판정: `PASS | FAIL | NOT_APPLICABLE`
- Evidence 경로:
- Before Commit SHA:
- After Commit SHA:
- 동일 조건 여부:
- 측정·재현 환경:
- 검증 한계:

| 핵심 지표·현상 | Before | After | 판정 |
|---|---|---|---|
|  |  |  | `PASS | FAIL | N/A` |

### 정합성 회귀 확인

-

## AI Usage

### AI가 한 일

- 코드 구조 분석:
- 문제·원인 후보:
- 대안 제안:
- 구현 초안:
- 테스트 후보:
- Diff Self Review:

### Human이 직접 판단·검증한 일

-

### AI 제안을 그대로 수용한 부분

- 제안:
- 수용 이유:

### AI 제안을 수정하거나 거부한 부분

- 제안:
- 최종 판단:
- 이유:

## 핵심 트러블슈팅

<!--
실제 의미 있는 문제 해결이 있었을 때만 작성합니다.
원본 구조: 문제 → 재현 → 원인 가설 → 확인 → 근본 원인 → 대안 비교 → 선택 → 구현 → 검증 → Before/After → 결과 → 한계
없으면 `해당 없음`과 이유를 작성합니다.
-->

- Troubleshooting 후보: `YES | NO`
- 이유:
- 원본 기록 경로:

## Human Understanding Gate

<!-- 개인 리팩토링 PR에서는 아래 질문에 Human이 자기 말로 답할 수 있어야 Merge합니다. -->

### Q1. 기존 구조는 어떻게 동작했고 왜 문제였는가?

**Human 답변:**

-

### Q2. 어떤 대안을 검토했고 왜 현재 방법을 선택했는가?

**Human 답변:**

-

### Q3. 이 변경과 관련된 핵심 Java/Spring 개념은 무엇인가?

**Human 답변:**

-

### Q4. 무엇으로 개선과 회귀 없음을 검증했는가?

**Human 답변:**

-

### Q5. 현재 구조의 한계와 trade-off는 무엇인가?

**Human 답변:**

-

## Interview Check

- [ ] 코드 없이 기존 실행 흐름을 설명할 수 있다.
- [ ] 왜 리팩토링했는지 설명할 수 있다.
- [ ] 검토한 대안과 선택 이유를 설명할 수 있다.
- [ ] 관련 Java/Spring 개념을 설명할 수 있다.
- [ ] 검증 방법과 결과를 설명할 수 있다.
- [ ] 현재 구조의 한계와 trade-off를 설명할 수 있다.
- [ ] AI가 작성한 핵심 코드도 내가 설명할 수 있다.

### 예상 꼬리질문

1.
2.
3.

## Output Gate

### Troubleshooting

- 후보: `YES | NO`
- 이유:
- 저장 위치:

### Tech Blog

<!-- 아래 중 2개 이상이면 후보로 검토합니다. -->

- [ ] 명확한 문제
- [ ] 원인 분석 과정
- [ ] 대안 비교
- [ ] 기술적 판단
- [ ] Before/After 또는 객관적 검증
- [ ] 다른 개발자에게 재사용 가치
- [ ] 기술면접 가치

- 후보: `YES | NO`
- 핵심 메시지:

### Interview Note

- 후보: `YES | NO`
- 정리할 핵심 개념:

### Portfolio Candidate

- 후보: `YES | NO`
- 문제 해결 역량을 보여주는 지점:

## 담당 구현 AI Review·반영 기록

- Review Skill: `skills/bobfull-pr-review/SKILL.md`
- 최신 Review 기준 Head:
- 최신 판정: `MERGEABLE | BLOCK | 미실행`
- 최신 PR Review 댓글:
- BLOCKER/MAJOR 반영 내용:
- MINOR/SUGGESTION 후속 처리:
- 리뷰 후 재실행 검증:

## Merge Gate

- [ ] Issue 범위 밖 변경 없음
- [ ] 관련 테스트 `PASS` 또는 해당 없음 근거 명확
- [ ] 전체 build `PASS` 또는 해당 없음 근거 명확
- [ ] 핵심 기능 직접 검증 `PASS` 또는 해당 없음 근거 명확
- [ ] 필요한 Before/After Evidence `PASS` 또는 `NOT_APPLICABLE` 근거 명확
- [ ] 필요한 SonarQube·JaCoCo 검증 완료
- [ ] 최신 Head 담당 구현 AI Review 완료
- [ ] 미해결 `BLOCKER` 없음
- [ ] 미해결 `MAJOR` 없음
- [ ] Human 결정 필요 사항 없음
- [ ] 개인 리팩토링 PR이면 Human Understanding Gate 완료
- [ ] AI가 작성한 핵심 코드를 Human이 설명할 수 있음
- [ ] Output Gate 판정 완료

최종 Merge는 Human이 수행합니다.
