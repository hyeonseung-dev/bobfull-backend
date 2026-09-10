---
name: bobfull-refactoring
description: BobFull 개인 리팩토링 Issue에서 현재 구조 이해, AI 분석, Human READY, 테스트 안전망, 리팩토링, 검증, 트러블슈팅, 기술면접, 기술블로그·포트폴리오 후보까지 하나의 흐름으로 운영할 때 사용한다.
---

# BobFull Personal Refactoring Skill

## 사용 시점

다음 작업은 이 Skill을 적용한다.

- Issue 유형이 `refactor`
- 팀 프로젝트 종료 후 개인 고도화
- SonarQube·JaCoCo 기준선을 기반으로 한 구조 개선
- 도메인·패키지 경계, 책임 분리, 복잡도·중복·조건식 개선

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

구현 전에 Human이 이해해야 할 내용을 먼저 정리한다.

- 요청 시작점
- 핵심 호출 흐름
- 트랜잭션 경계
- 상태 변경 위치
- 외부 I/O
- 의존 방향
- 변경 시 회귀 위험

Human이 설명하지 못하는 핵심 항목이 있으면 구현을 시작하지 않고 학습용 설명과 질문을 제공한다.

## 3. AI 역할

AI는 다음을 적극적으로 수행한다.

- 코드 구조 분석
- 문제 후보와 원인 가설
- 대안 비교
- 테스트 후보
- 구현 초안
- 최신 Diff Self Review
- 면접 꼬리질문
- Troubleshooting·Tech Blog 초안 구조화

다음은 Human 결정으로 남긴다.

- 실제 문제 여부
- 도메인 정책
- API·DB 계약
- 트랜잭션 경계
- 최종 대안 선택
- 측정 결과 해석
- 최종 Merge

## 4. Human READY Gate

코드 수정 전에 Human이 최소한 다음을 자기 말로 설명해야 한다.

1. 무엇을 바꾸는가?
2. 왜 문제인가?
3. 무엇은 바뀌면 안 되는가?
4. 어떤 대안을 선택하는가?
5. 왜 그 대안인가?
6. 무엇으로 검증하는가?

핵심 이해가 부족하면 `status:human-answer-required`로 중단하고 설명·질문을 제공한다.

## 5. Refactor 규칙

- 한 Issue 한 목적
- 기능 추가 혼합 금지
- 정책 변경 혼합 금지
- 불필요한 패턴·추상화 금지
- 외부 동작 유지
- Issue 범위 이탈 시 중단
- AI가 생성했더라도 Human이 설명할 수 없는 코드는 완료로 취급하지 않음

## 6. Verify

최소 기준:

```text
관련 테스트 PASS
+ 전체 build PASS
+ 핵심 동작 확인
+ 필요한 SonarQube / JaCoCo 비교
+ API·DB·비즈니스 정책 회귀 없음
```

Before/After는 동일 조건을 우선한다.

## 7. Troubleshooting Gate

다음 구조가 있으면 [`docs/refactoring/TROUBLESHOOTING_TEMPLATE.md`](../../docs/refactoring/TROUBLESHOOTING_TEMPLATE.md) 형식으로 기록한다.

```text
문제 → 재현 → 원인 가설 → 확인 → 근본 원인 → 대안 비교 → 선택 → 구현 → 검증 → Before/After → 결과 → 한계
```

단순 정리는 트러블슈팅으로 과장하지 않는다.

## 8. PR 작성

PR에는 최소한 다음을 남긴다.

- Why
- Before
- What
- Why this approach
- Verification
- Before / After
- AI Usage
- AI 제안을 수용·수정·거부한 근거
- Interview Check
- Output Gate

## 9. AI Self Review

최신 Head와 Diff를 기준으로 다시 검토한다.

- 범위 이탈
- 회귀
- 트랜잭션·정합성 문제
- 테스트 누락
- 과도한 추상화
- SonarQube 점수만 맞추는 부자연스러운 변경
- 설명하기 어려운 AI 생성 코드

BLOCKER·MAJOR는 수정 후 재검증한다.

## 10. Human Understanding Gate

Merge 전에 Human이 코드 없이 다음을 답할 수 있어야 한다.

- 기존 구조
- 문제 원인
- 검토한 대안
- 선택 이유
- 관련 Java/Spring 개념
- 검증 방법
- 현재 한계
- 주요 trade-off

답하지 못하면 Merge 단계로 넘기지 않는다.

## 11. Output Gate

작업 종료 시 반드시 다음을 판정한다.

- Troubleshooting: `YES | NO`
- Tech Blog: `YES | NO`
- Interview Note: `YES | NO`
- Portfolio Candidate: `YES | NO`

`YES`라면 이유와 저장 위치를 PR에 남긴다.
