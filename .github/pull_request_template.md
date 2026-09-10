## 요약

무엇을 바꿨는지 2~4문장으로 적습니다.

- Closes #
- 운영 모드: `Personal Refactoring Mode | V3 Sprint Mode`

## 변경 내용

-

### 변경하지 않은 계약

- API:
- DB Schema:
- 비즈니스 정책:
- 상태 전이:
- 그 외 반드시 유지할 동작:

## 왜 이렇게 했나

- 기존 문제:
- 선택한 방법:
- 다른 방법을 쓰지 않은 이유:

## 검증

| 항목 | 결과 | 근거 |
|---|---|---|
| 관련 테스트 | `PASS | FAIL | N/A` |  |
| 전체 테스트 | `PASS | FAIL | N/A` |  |
| 전체 build | `PASS | FAIL | N/A` |  |
| 핵심 동작 확인 | `PASS | FAIL | N/A` |  |
| SonarQube | `PASS | FAIL | NOT_RUN | N/A` |  |
| JaCoCo | `PASS | FAIL | NOT_RUN | N/A` |  |

### Before / After

의미 있는 변화만 작성합니다. 수치 비교가 의미 없으면 이유를 적습니다.

| 지표 또는 현상 | Before | After |
|---|---|---|
|  |  |  |

## AI 활용

- AI가 한 일:
- Human이 결정한 일:
- AI Self Review: `MERGEABLE | BLOCK | 미실행`
- BLOCKER:
- MAJOR:

## 면접용 Q&A

<!--
Human에게 추상적인 질문지를 작성하게 하지 않습니다.
AI가 이 PR의 실제 변경에 맞는 쉬운 질문 2~3개와 답안을 함께 작성합니다.
답안은 실제 코드, Issue, 검증 결과를 근거로 작성하고 모르는 내용은 채우지 않습니다.
Human은 답안을 읽고 틀린 내용을 수정하거나 이해가 안 되는 부분만 질문합니다.
-->

### Q1. [이번 변경을 가장 쉽게 설명하는 질문]

**답:**

-

### Q2. [왜 이 방법을 선택했는지 묻는 질문]

**답:**

-

### Q3. [검증 또는 핵심 기술 개념을 묻는 질문, 필요할 때만]

**답:**

-

### Human 이해 확인

- [ ] 위 Q&A의 내용이 실제 변경과 일치한다.
- [ ] 코드 없이도 위 답변 정도는 설명할 수 있다.
- [ ] 이해되지 않는 AI 생성 코드가 없다.

별도 장문 답변은 작성하지 않습니다. 이해가 안 되는 부분이 있을 때만 학습 후 다시 확인합니다.

## Output Gate

- Troubleshooting: `YES | NO`
- Tech Blog: `YES | NO`
- Interview Note: `YES | NO`
- Portfolio Candidate: `YES | NO`

필요한 경우 이유와 저장 위치만 짧게 적습니다.

## Merge Gate

- [ ] Issue 범위 밖 변경 없음
- [ ] 필요한 테스트와 build 검증 완료
- [ ] 필요한 Before / After 검증 완료 또는 N/A 근거 명확
- [ ] 최신 Head AI Self Review 완료
- [ ] 미해결 BLOCKER 없음
- [ ] 미해결 MAJOR 없음
- [ ] Human 이해 확인 완료
- [ ] Output Gate 판정 완료

최종 Merge는 Human이 수행합니다.
