# BobFull 개인 리팩토링 워크플로우

## 목적

팀 프로젝트 종료 후 개인 고도화 단계에서는 리팩토링 자체보다 **근거를 남기며 이해하고 개선하는 과정**을 우선한다.

한 번의 리팩토링 작업이 다음 결과로 이어지게 한다.

- GitHub Issue와 PR의 검증 가능한 기록
- 트러블슈팅 원본
- 기술면접 대비용 이해와 꼬리질문
- 기술블로그 후보
- 포트폴리오 후보
- AI 활용 과정에서 Human이 내린 판단 기록

## 최우선 원칙

> AI가 작성하거나 제안한 코드라도 Human이 직접 설명할 수 없으면 Merge하지 않는다.

추가 원칙:

- 한 Issue와 PR에는 하나의 주요 목적만 둔다.
- 리팩토링과 기능 추가, 성능 개선, 정책 변경을 가능한 한 분리한다.
- `좋아 보인다`가 아니라 테스트와 측정으로 개선 여부를 확인한다.
- Before/After가 의미 없는 변경은 억지 수치를 만들지 않고 `NOT_APPLICABLE` 근거를 남긴다.
- 기존 동작을 유지하는 리팩토링에서 API·DB·비즈니스 정책 변경이 필요하면 별도 Issue로 분리한다.

## 전체 흐름

```text
0. BASELINE
   Test / SonarQube / JaCoCo / 필요한 측정값 확보
        ↓
1. ISSUE
   문제·근거·영향 범위·완료 조건·검증 방법 정의
        ↓
2. UNDERSTAND
   현재 코드의 실행 흐름·책임·트랜잭션·의존관계 이해
        ↓
3. AI ANALYSIS
   원인 후보·대안·위험·테스트 포인트·구현 초안 탐색
        ↓
4. HUMAN READY GATE
   AI 설명 없이 왜 바꾸는지 Human이 직접 설명
        ↓
5. TEST SAFETY NET
   기존 동작 재현 및 필요한 테스트 보강
        ↓
6. REFACTOR
   한 가지 목적만 변경
        ↓
7. VERIFY
   Test / SonarQube / JaCoCo / 직접 동작 / 정합성 검증
        ↓
8. TROUBLESHOOTING GATE
   문제 → 원인 → 판단 → 해결 → 검증 구조가 있으면 원본 기록
        ↓
9. DRAFT PR
   Why / What / Evidence / Before-After / AI Usage / Interview Check
        ↓
10. AI SELF REVIEW
    최신 Diff 기준 결함·회귀·과도한 추상화·범위 이탈 검토
        ↓
11. UNDERSTANDING GATE
    코드 없이 기존 구조·변경 이유·대안·핵심 개념·한계 설명
        ↓
12. HUMAN MERGE
        ↓
13. OUTPUT
    Troubleshooting / Tech Blog / Interview / Portfolio 후보 판단
```

## 0. Baseline

리팩토링 전에 현재 상태를 먼저 남긴다.

기본 확인:

- 관련 테스트 결과
- 전체 build 결과
- JaCoCo Coverage
- SonarQube Quality Gate와 주요 Issue
- 필요한 경우 Complexity, Duplication, Code Smell
- 성능·동시성·정합성 문제라면 해당 문제의 재현 결과

모든 작업에 모든 지표가 필요한 것은 아니다. 현재 Issue의 개선 목적과 직접 관련된 지표만 사용한다.

## 1. Issue

리팩토링은 `.github/ISSUE_TEMPLATE/refactor.md`로 시작한다.

Issue에서 구현 전에 다음을 확정한다.

- 현재 구조와 문제
- 문제라고 판단한 근거
- 현재 코드 흐름에 대한 Human의 이해
- 해결 후보와 trade-off
- AI에게 맡길 영역과 Human이 직접 판단할 영역
- 변경하지 않을 계약
- Before/After 검증 계획
- 면접에서 설명해야 할 질문
- 완료 조건

## 2. Understand

AI에게 구현을 맡기기 전에 Human이 현재 코드를 먼저 이해한다.

최소한 다음을 설명할 수 있어야 한다.

1. 요청은 어디서 시작하는가?
2. 어떤 Service·Domain·Repository를 거치는가?
3. 트랜잭션은 어디서 시작하고 끝나는가?
4. 상태 변경과 외부 I/O는 어디서 일어나는가?
5. 현재 책임이 왜 과하거나 잘못 배치됐다고 판단하는가?
6. 변경 시 깨질 수 있는 동작은 무엇인가?

설명하지 못하는 항목은 Issue의 `학습 필요` 영역에 남기고 먼저 확인한다.

## 3. AI Analysis

AI는 빠른 탐색과 초안을 만드는 도구로 사용한다.

AI에게 맡길 수 있는 것:

- 관련 코드 구조 분석
- 문제 후보와 원인 가설 제시
- 해결 대안 A/B/C 비교
- 테스트 케이스 후보
- 리팩토링 코드 초안
- Diff Self Review
- 기술면접 꼬리질문 생성
- 트러블슈팅·블로그 초안 구조화

Human이 직접 책임질 것:

- 실제로 문제가 맞는지 판단
- 도메인 정책
- 트랜잭션 경계
- API·DB 계약
- 최종 구조 선택
- 테스트 결과 해석
- AI 제안 수용·수정·거부 결정
- 최종 Merge

AI 제안을 그대로 사용했으면 수용 이유를, 수정하거나 거부했으면 그 이유를 PR에 남긴다.

## 4. Human READY Gate

코드 수정 전 Human은 다음 질문에 자기 말로 답할 수 있어야 한다.

- 무엇을 바꾸려고 하는가?
- 왜 지금 구조가 문제인가?
- 어떤 동작은 절대 바뀌면 안 되는가?
- 어떤 방법을 선택하려고 하는가?
- 왜 다른 방법보다 이 방법이 적절한가?
- 어떻게 성공 여부를 검증할 것인가?

핵심 질문에 답하지 못하면 구현을 시작하지 않고 학습·분석 단계로 돌아간다.

## 5. Test Safety Net

리팩토링 전 기존 동작을 보호할 테스트가 있는지 확인한다.

- 핵심 비즈니스 규칙
- 상태 전이
- 트랜잭션 rollback
- 동시성·멱등성
- 외부 PG·Kafka·Outbox 등 경계
- 정상·실패·경계 시나리오

테스트가 부족하면 리팩토링과 섞지 말고 필요한 최소 안전망부터 추가한다.

## 6. Refactor

원칙:

- 한 Issue에서 한 가지 목적만 변경한다.
- 기능 추가를 섞지 않는다.
- 의미 없는 디자인 패턴 적용을 하지 않는다.
- AI가 제안했다는 이유만으로 추상화를 추가하지 않는다.
- 기존 외부 동작을 유지한다.
- 코드 수정 범위가 Issue 계약을 넘어가면 중단하고 Issue를 분리한다.

## 7. Verify

최소 검증:

```text
관련 테스트 PASS
+ 전체 build PASS
+ 변경 핵심 동작 직접 확인
+ 필요한 SonarQube / JaCoCo 비교
+ 비즈니스 정책·API·DB 회귀 없음 확인
```

Before/After는 가능한 한 동일한 조건으로 비교한다.

예:

- Cognitive Complexity 감소
- 중복 코드 감소
- 테스트 Coverage 유지 또는 증가
- SonarQube Issue 감소
- 기존 주요 시나리오 전체 통과

수치 개선보다 구조 개선이 핵심인 경우에는 `책임 경계`, `의존 방향`, `테스트 가능성`, `변경 영향 범위`를 근거로 설명한다.

## 8. Troubleshooting Gate

모든 리팩토링을 트러블슈팅으로 포장하지 않는다.

다음 흐름이 존재하면 `docs/refactoring/TROUBLESHOOTING_TEMPLATE.md`로 기록한다.

```text
문제 상황
→ 재현
→ 원인 가설
→ 확인 과정
→ 근본 원인
→ 해결 후보 비교
→ 선택과 이유
→ 구현
→ 검증
→ Before / After
→ 결과
→ 한계와 배운 점
```

단순 네이밍 변경, 포맷 정리, 의미 없는 중복 제거는 트러블슈팅으로 승격하지 않는다.

## 9. PR

PR은 최종 검문소 역할을 한다.

반드시 확인할 내용:

- Why
- Before
- What
- Why this approach
- Verification
- Before / After
- AI Usage
- AI 제안 수용·수정·거부 근거
- Interview Check
- Troubleshooting / Tech Blog / Portfolio 후보 여부

## 10. AI Self Review

Draft PR 생성 후 AI는 구현자 역할에서 리뷰어 역할로 전환해 최신 Head와 Diff를 다시 본다.

주요 점검:

- Issue 범위 밖 변경
- 기능 회귀
- 트랜잭션·상태·정합성 문제
- 테스트 누락
- 과도한 추상화
- 불필요한 패턴 적용
- 이름만 바꾼 리팩토링
- SonarQube 점수만 맞추기 위한 부자연스러운 코드
- AI 생성 흔적 때문에 설명하기 어려운 구조

BLOCKER 또는 MAJOR가 있으면 수정 후 다시 검증한다.

## 11. Understanding Gate

Merge 전 Human은 코드 없이 다음에 답할 수 있어야 한다.

1. 기존 구조는 어떻게 동작했는가?
2. 왜 문제가 되었는가?
3. 어떤 대안을 검토했는가?
4. 왜 현재 방법을 선택했는가?
5. 관련 Java/Spring 개념은 무엇인가?
6. 어떻게 검증했는가?
7. 현재 구조의 한계는 무엇인가?
8. 면접관이 반대 대안을 제시하면 어떤 trade-off로 설명할 것인가?

답하지 못하는 항목이 있으면 학습 후 다시 확인한다.

## 12. Output Gate

Merge 후 작업을 다음 네 가지로 분류한다.

### Troubleshooting

문제 재현·원인 분석·해결 판단·검증 과정이 명확하면 기록한다.

### Tech Blog

아래 조건 중 2개 이상이면 기술블로그 후보로 분류한다.

- 명확한 문제가 있었다.
- 원인 분석 과정이 있었다.
- 해결 대안을 비교했다.
- 기술적 판단이 있었다.
- Before/After 또는 객관적인 검증 결과가 있다.
- 다른 개발자에게도 재사용 가치가 있다.
- 기술면접에서 질문받을 가치가 있다.

모든 Issue를 블로그로 만들지 않는다.

### Interview

핵심 개념, 선택 이유, trade-off, 검증 방법, 한계를 60~90초 답변과 꼬리질문 형태로 정리한다.

### Portfolio

단순 코드 정리가 아니라 문제 해결 능력과 기술적 판단을 보여줄 수 있는 사례만 후보로 올린다.

## 현재 BobFull 적용 순서

개인 리팩토링의 첫 사이클은 다음 순서로 진행한다.

```text
SonarQube + JaCoCo Before 기준선 확정
→ 테스트 안전망 점검
→ 도메인·패키지 경계
→ 클래스·메서드 책임 분리
→ 중복·네이밍·조건식 정리
→ SonarQube 잔여 Issue 선별 처리
→ 동일 조건 최종 재측정
→ 대표 Troubleshooting / Tech Blog / Interview / Portfolio 산출물 정리
```

현재 작성한 SonarQube·JaCoCo 기준선 작업 자체를 첫 번째 완료 사례로 사용한다.
