---
name: bobfull-refactoring
description: BobFull 개인 리팩토링 Issue에서 문제 정의, AI 분석, Human 판단, 테스트, 리팩토링, 검증, 결과 중심 PR, 면접 Q&A까지 하나의 흐름으로 운영할 때 사용한다.
---

# BobFull Personal Refactoring Skill

## 사용 시점

다음 작업은 이 Skill을 적용한다.

- Issue 유형이 `refactor`
- 팀 프로젝트 종료 후 개인 고도화
- SonarQube와 JaCoCo 기준선을 기반으로 한 구조 개선
- 도메인, 패키지 경계, 책임 분리, 복잡도, 중복, 조건식 개선

세부 기준은 [`docs/PERSONAL_REFACTORING_WORKFLOW.md`](../../docs/PERSONAL_REFACTORING_WORKFLOW.md)를 따른다.

## 핵심 원칙

- Issue는 분석과 구현 과정의 작업 공간이다.
- PR은 최종 결과 문서다.
- PR 첫 화면에는 결과가 먼저 나와야 한다.
- 코드 변경 PR에는 실제 코드 Before / After를 반드시 넣는다.
- 구현 과정을 길게 복사하지 않는다.
- AI가 생성한 핵심 코드라도 Human이 설명할 수 없으면 완료로 취급하지 않는다.

## 필수 흐름

```text
Baseline
→ Issue 계약
→ 현재 구조 이해
→ AI 분석과 Human 판단
→ 테스트 안전망
→ Refactor
→ Verify
→ Draft PR
→ 실제 코드 Before / After
→ AI Self Review
→ 면접용 Q&A
→ Human 확인
→ Human Merge
→ 필요한 Output
```

## 1. 구현 전

변경 전에 아래를 확보한다.

- 관련 테스트
- 전체 build
- 현재 Issue에 필요한 SonarQube 또는 JaCoCo 지표
- 문제를 보여주는 코드나 구조
- 변경하지 않을 API, DB, 정책, 상태 전이

Human에게는 이미 결정된 내용을 반복해서 묻지 않는다. 실제 판단이 필요한 항목만 확인한다.

## 2. AI 역할

AI가 적극적으로 수행할 것:

- 코드 구조 분석
- 문제 후보와 원인 가설
- 대안 비교
- 테스트 후보
- 구현 초안
- 최신 Diff Self Review
- PR용 실제 Before / After 추출
- 면접 질문과 답안 작성

Human이 책임질 것:

- 실제 문제 여부
- 작업 범위
- 도메인 정책
- API와 DB 계약
- 트랜잭션 경계
- 최종 대안 선택
- 측정 결과 해석
- 최종 Merge

## 3. Refactor 규칙

- 한 Issue 한 목적
- 기능 추가 혼합 금지
- 정책 변경 혼합 금지
- 불필요한 패턴과 추상화 금지
- 외부 동작 유지
- Issue 범위 이탈 시 중단

## 4. Verify

최소 기준:

```text
관련 테스트 PASS
+ 전체 build PASS
+ 핵심 동작 확인
+ 필요한 SonarQube 또는 JaCoCo 비교
+ API, DB, 비즈니스 정책 회귀 없음
```

Before와 After는 동일 조건을 우선한다.

## 5. PR 작성 규칙

PR은 아래 순서를 고정한다.

```text
결과
→ 실제 코드 Before / After
→ 전체 변화 요약
→ 왜 바꿨나
→ 검증
→ AI 활용과 최종 판단
→ 면접용 Q&A
→ Output
```

### 결과를 먼저 쓴다

첫 섹션에서 한 줄로 `Before → After`를 보여준다.

예:

```text
reservation 주석 marker 186 → 172
코드 반복 JavaDoc 14개 제거
실행 코드 변경 없음
```

### 실제 코드 Before / After는 필수다

코드 변경 PR에서는 실제 Diff에서 대표적인 변경 1~3개를 가져온다.

좋은 예:

**Before**

```java
/** OWNER의 식당별 예약 목록 조회를 담당한다. */
@RestController
public class RestaurantReservationController {
```

**After**

```java
@RestController
public class RestaurantReservationController {
```

설명:

```text
클래스 이름과 매핑으로 이미 알 수 있는 역할 설명을 제거했다.
```

구조 변경이라면 실제 메서드나 의존 구조의 Before / After를 보여준다. 설명만으로 대체하지 않는다.

### 구현 과정은 Issue에 둔다

시도한 명령, 중간 HOLD, 후보 탐색, 세부 구현 로그는 Issue에 둔다. PR에서는 최종 판단과 결과만 남긴다.

## 6. AI Self Review

최신 Head와 Diff를 기준으로 다시 검토한다.

- 범위 이탈
- 회귀
- 트랜잭션과 정합성 문제
- 테스트 누락
- 과도한 추상화
- 설명하기 어려운 AI 생성 코드
- PR의 Before / After가 실제 Diff와 일치하는지
- PR 첫 화면에서 결과가 바로 이해되는지

BLOCKER와 MAJOR는 수정 후 재검증한다.

## 7. 면접용 Q&A

Human에게 긴 서술형 답변을 요구하지 않는다.

AI가 실제 결과를 기준으로 쉬운 질문 2~3개와 답안을 함께 작성한다.

예:

- 이번에 무엇이 달라졌나요?
- 왜 이렇게 바꿨나요?
- 안전하게 바뀌었다는 것을 어떻게 확인했나요?

Human은 답안이 실제 변경과 맞는지, 본인이 설명 가능한지만 확인한다.

## 8. Output

PR 자체는 항상 `결과 → Before / After → 이유 → 검증` 순서의 트러블슈팅형 구조를 사용한다.

별도 Troubleshooting 문서는 문제 재현과 원인 분석 가치가 충분한 경우에만 만든다.

작업 종료 시 다음을 판정한다.

- Troubleshooting 기록: `YES | NO`
- Tech Blog: `YES | NO`
- Interview Note: `YES | NO`
- Portfolio Candidate: `YES | NO`

최종 Merge는 Human이 수행한다.
