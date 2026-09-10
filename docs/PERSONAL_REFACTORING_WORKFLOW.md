# BobFull 개인 리팩토링 워크플로우

## 목적

팀 프로젝트 종료 후 개인 고도화에서는 구현 과정을 길게 설명하는 것보다 **무엇이 어떻게 달라졌는지 결과를 명확하게 보여주는 것**을 우선한다.

Issue는 분석과 판단 과정의 작업 공간으로 사용하고, PR은 최종 결과 문서로 사용한다.

한 번의 리팩토링 작업이 다음 결과로 이어지게 한다.

- GitHub Issue의 문제 분석과 검증 기록
- 결과 중심 PR
- 실제 코드 Before / After
- 기술면접 대비용 Q&A
- 필요한 경우 별도 Troubleshooting, Tech Blog, Portfolio 후보

## 최우선 원칙

> PR을 열었을 때 구현 과정보다 결과가 먼저 보여야 한다.

> AI가 작성하거나 제안한 핵심 코드라도 Human이 설명할 수 없으면 Merge하지 않는다.

추가 원칙:

- 한 Issue와 PR에는 하나의 주요 목적만 둔다.
- 리팩토링과 기능 추가, 성능 개선, 정책 변경을 가능한 한 분리한다.
- 테스트와 필요한 측정으로 회귀 여부를 확인한다.
- 의미 없는 수치는 억지로 만들지 않는다.
- API, DB, 비즈니스 정책 변경이 필요하면 별도 Issue로 분리한다.

## 전체 흐름

```text
Baseline
→ Issue에서 문제와 범위 정의
→ 현재 구조 이해
→ AI 분석과 Human 판단
→ Test Safety Net
→ Refactor
→ Verify
→ Draft PR
→ 실제 코드 Before / After 작성
→ AI Self Review
→ 면접용 Q&A 자동 작성
→ Human 확인
→ Human Merge
→ 필요한 산출물만 추가
```

## 1. Issue는 과정 기록

리팩토링은 `.github/ISSUE_TEMPLATE/refactor.md`로 시작한다.

Issue에서 구현 전에 최소한 다음을 정리한다.

- 무엇이 문제인지
- 첫 작업 범위
- 무엇은 바뀌면 안 되는지
- 어떤 방법으로 바꿀지
- 무엇으로 검증할지
- AI와 Human의 역할

분석 과정, 후보 비교, 테스트 시도, HOLD 같은 중간 기록은 Issue에 남긴다. PR에는 이 과정을 반복해서 길게 옮기지 않는다.

## 2. Understand와 Human READY

구현 전에 이번 변경과 직접 관련된 코드 흐름과 회귀 위험을 이해한다.

필요한 판단이 남아 있을 때만 Human에게 확인한다.

- 무엇을 바꾸는가
- 무엇은 절대 바뀌면 안 되는가
- 어떤 방법으로 바꿀 것인가
- 무엇으로 검증할 것인가

이미 Issue와 대화에서 정해진 내용은 다시 묻지 않는다.

## 3. Refactor와 Verify

- 한 Issue에서 한 가지 목적만 변경한다.
- 기능 추가를 섞지 않는다.
- 의미 없는 패턴과 추상화를 추가하지 않는다.
- 기존 외부 동작을 유지한다.
- 범위가 커지면 별도 Issue로 분리한다.

기본 검증은 아래를 따른다.

```text
관련 테스트 PASS
+ 전체 build PASS
+ 핵심 동작 확인
+ 필요한 SonarQube 또는 JaCoCo 비교
+ API, DB, 비즈니스 정책 회귀 없음
```

Before와 After는 가능한 한 동일한 조건으로 비교한다.

## 4. PR은 결과 문서

PR은 트러블슈팅형 구조로 작성하되, 구현 과정보다 결과를 먼저 보여준다.

고정 순서:

```text
결과
→ 실제 코드 Before / After
→ 전체 변화 요약
→ 왜 바꿨는가
→ 검증
→ AI 활용과 최종 판단
→ 면접용 Q&A
→ Output
```

### 결과

PR 첫 화면에서 바로 이해할 수 있어야 한다.

예:

```text
reservation 주석 marker 186 → 172
코드가 이미 설명하는 JavaDoc 14개 제거
실행 코드와 정책 주석은 변경 없음
```

### 실제 코드 Before / After

코드 변경 PR에서는 실제 Diff에서 대표 코드 1~3개를 가져온다. 추상적인 설명만 쓰지 않는다.

예:

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

그리고 한 줄로 결과를 설명한다.

```text
클래스 이름과 매핑으로 이미 드러나는 역할 설명을 제거했다.
```

구조 리팩토링이라면 메서드 또는 의존 구조의 Before / After를 실제 코드로 보여준다. 문서나 설정만 바뀐 PR이면 코드 예시는 N/A로 두고 실제 문서 또는 설정 Diff를 보여준다.

### 전체 변화 요약

정량 수치가 의미 있으면 표로 비교한다. 의미 없으면 구조적 변화만 적는다.

## 5. AI Self Review

Draft PR 생성 후 최신 Head와 Diff를 다시 본다.

- Issue 범위 밖 변경
- 기능 회귀
- 트랜잭션과 정합성 문제
- 테스트 누락
- 과도한 추상화
- 설명하기 어려운 AI 생성 코드
- PR의 Before / After가 실제 Diff와 일치하는지

BLOCKER와 MAJOR가 있으면 수정 후 다시 검증한다.

## 6. 면접용 Q&A

Human에게 추상적인 서술형 질문지를 작성하게 하지 않는다.

AI가 해당 PR의 실제 결과를 기준으로 쉬운 질문 2~3개와 답안을 함께 작성한다.

예시:

- 이번에 무엇이 달라졌나요?
- 왜 이 부분만 바꿨나요?
- 안전하게 바뀌었다는 것은 어떻게 확인했나요?

Human은 답안 내용이 실제 변경과 맞는지, 본인이 설명 가능한지만 확인한다. 별도 장문 답변은 요구하지 않는다.

## 7. Troubleshooting과 Output

**PR 자체는 항상 `결과 → Before / After → 이유 → 검증` 순서의 트러블슈팅형 문서로 작성한다.**

다만 별도 Troubleshooting 문서는 아래 흐름이 충분히 있을 때만 만든다.

```text
문제 재현
→ 원인 가설
→ 확인
→ 근본 원인
→ 대안 비교
→ 선택
→ 구현
→ 검증
→ 결과
```

단순 주석 정리, 네이밍 변경, 포맷 정리처럼 원인 분석 가치가 낮은 작업은 별도 Troubleshooting 문서나 Tech Blog로 과장하지 않는다.

작업 종료 시 다음만 판정한다.

- Troubleshooting 기록: YES / NO
- Tech Blog: YES / NO
- Interview Note: YES / NO
- Portfolio Candidate: YES / NO

## 8. Human Merge

AI는 최종 Merge하지 않는다. Human은 PR의 아래 세 가지만 우선 확인한다.

1. Before / After가 실제 코드와 맞는가
2. 결과와 검증이 이해되는가
3. 면접용 Q&A를 본인이 설명할 수 있는가

이상이 없으면 Human이 직접 Merge한다.

## 현재 BobFull 적용 순서

```text
SonarQube + JaCoCo Before 기준선 확정
→ 테스트 안전망 점검
→ 도메인과 패키지 경계
→ 클래스와 메서드 책임 분리
→ 중복, 네이밍, 조건식 정리
→ SonarQube 잔여 Issue 선별 처리
→ 동일 조건 최종 재측정
→ 대표 Troubleshooting, Tech Blog, Interview, Portfolio 산출물 정리
```

Baseline Issue #2는 완료된 공통 기준선으로 사용한다.
