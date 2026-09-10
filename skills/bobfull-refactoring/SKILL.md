---
name: bobfull-refactoring
description: BobFull 개인 리팩토링 Issue에서 현재 구조 이해, AI 분석, Human READY, 테스트 안전망, 리팩토링, 검증, 트러블슈팅, 면접 Q&A, 기술블로그와 포트폴리오 후보까지 하나의 흐름으로 운영할 때 사용한다.
---

# BobFull Personal Refactoring Skill

## 사용 시점

다음 작업은 이 Skill을 적용한다.

- Issue 유형이 `refactor`
- 팀 프로젝트 종료 후 개인 고도화
- SonarQube와 JaCoCo 기준선을 기반으로 한 구조 개선
- 도메인, 패키지 경계, 책임 분리, 복잡도, 중복, 조건식 개선

세부 기준은 [`docs/PERSONAL_REFACTORING_WORKFLOW.md`](../../docs/PERSONAL_REFACTORING_WORKFLOW.md)를 따른다.

## 필수 순서

```text
Baseline
→ Issue 계약
→ 현재 구조 이해
→ AI 분석
→ Human READY
→ 테스트 안전망
→ Refactor
→ Verify
→ Troubleshooting Gate
→ Draft PR
→ AI Self Review
→ Human Understanding Gate
→ Human Merge
→ Output Gate
```

순서를 생략하지 않는다.

## 1. Baseline

변경 전에 현재 상태를 확보한다.

- 관련 테스트
- 전체 build
- 필요한 SonarQube 지표
- 필요한 JaCoCo 지표
- Issue가 주장하려는 문제의 재현 결과

현재 Issue에 의미 없는 수치는 만들지 않는다.

## 2. 현재 구조 이해

구현 전에 다음을 먼저 정리한다.

- 요청 시작점
- 핵심 호출 흐름
- 트랜잭션 경계
- 상태 변경 위치
- 외부 I/O
- 의존 방향
- 변경 시 회귀 위험

Human이 핵심 구조를 전혀 이해하지 못하면 구현을 시작하지 않는다. AI는 먼저 쉬운 설명과 필요한 질문을 제공한다.

## 3. AI 역할

AI가 적극적으로 수행할 것:

- 코드 구조 분석
- 문제 후보와 원인 가설
- 대안 비교
- 테스트 후보
- 구현 초안
- 최신 Diff Self Review
- 면접 질문과 답안 초안
- Troubleshooting과 Tech Blog 초안 구조화

Human이 책임질 것:

- 실제 문제 여부
- 도메인 정책
- API와 DB 계약
- 트랜잭션 경계
- 최종 대안 선택
- 측정 결과 해석
- 최종 Merge

## 4. Human READY Gate

코드 수정 전에는 아래를 짧게 확인한다.

1. 무엇을 바꾸는가?
2. 무엇은 절대 바뀌면 안 되는가?
3. 어떤 방법으로 바꿀 것인가?
4. 무엇으로 검증할 것인가?

필요한 판단이 남아 있으면 `status:human-answer-required`로 중단한다. 이미 Issue와 대화에서 답이 정해졌다면 같은 질문을 반복하지 않는다.

## 5. Refactor 규칙

- 한 Issue 한 목적
- 기능 추가 혼합 금지
- 정책 변경 혼합 금지
- 불필요한 패턴과 추상화 금지
- 외부 동작 유지
- Issue 범위 이탈 시 중단
- AI가 생성했더라도 Human이 설명할 수 없는 핵심 코드는 완료로 취급하지 않음

## 6. Verify

최소 기준:

```text
관련 테스트 PASS
+ 전체 build PASS
+ 핵심 동작 확인
+ 필요한 SonarQube 또는 JaCoCo 비교
+ API, DB, 비즈니스 정책 회귀 없음
```

Before와 After는 동일 조건을 우선한다.

## 7. Troubleshooting Gate

다음 구조가 있으면 [`docs/refactoring/TROUBLESHOOTING_TEMPLATE.md`](../../docs/refactoring/TROUBLESHOOTING_TEMPLATE.md) 형식으로 기록한다.

```text
문제 → 재현 → 원인 가설 → 확인 → 근본 원인 → 대안 비교 → 선택 → 구현 → 검증 → Before/After → 결과 → 한계
```

단순 정리는 트러블슈팅으로 과장하지 않는다.

## 8. PR 작성

PR에는 최소한 다음을 남긴다.

- 무엇을 바꿨는지
- 왜 바꿨는지
- 변경하지 않은 계약
- 검증 결과
- 필요한 Before와 After
- AI가 한 일과 Human이 결정한 일
- 면접용 Q&A
- Output Gate

PR을 기록용 문서보다 실제 검토에 쓰기 쉽게 유지한다. 의미 없는 빈 섹션은 만들지 않는다.

## 9. AI Self Review

최신 Head와 Diff를 기준으로 다시 검토한다.

- 범위 이탈
- 회귀
- 트랜잭션과 정합성 문제
- 테스트 누락
- 과도한 추상화
- SonarQube 점수만 맞추는 부자연스러운 변경
- 설명하기 어려운 AI 생성 코드

BLOCKER와 MAJOR는 수정 후 재검증한다.

## 10. Human Understanding Gate

Merge 전에 Human에게 긴 서술형 답변을 요구하지 않는다.

AI가 PR의 실제 변경을 기준으로 다음 형식의 쉬운 질문 2~3개와 답안을 함께 작성한다.

- 이번에 무엇을 바꿨는가?
- 왜 이 방법을 선택했는가?
- 어떻게 안전하게 바뀌었다고 확인했는가?
- 필요하면 해당 PR의 핵심 Java/Spring 개념 한 가지

질문은 고정 문구를 억지로 채우지 않고 실제 변경에 맞게 만든다.

Human은 다음만 확인한다.

1. 답안이 실제 변경과 맞는가?
2. 코드 없이 이 정도 내용을 설명할 수 있는가?
3. 이해되지 않는 AI 생성 핵심 코드가 없는가?

틀린 내용은 수정하고 이해가 안 되는 부분만 다시 학습한다. 별도 장문 답변은 요구하지 않는다.

## 11. Output Gate

작업 종료 시 다음을 판정한다.

- Troubleshooting: `YES | NO`
- Tech Blog: `YES | NO`
- Interview Note: `YES | NO`
- Portfolio Candidate: `YES | NO`

`YES`라면 이유와 저장 위치를 PR에 남긴다.
