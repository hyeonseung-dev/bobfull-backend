# Issue #14 — 인기 회차 성능 개선 개인 기여 범위 감사

## 결론

김현승의 기여 범위는 **B. 설계·검증에 실질 참여, 핵심 구현은 타인**으로 판정한다.

따라서 팀 프로젝트의 `SQL 83 → 7`, Stress p95 `13.14s → 1.34s`를 개인 단독 성과로
표현하지 않는다. 개인 포트폴리오에는 팀 성능 개선의 검증·근거 보강·결과 해석에 참여한 사실과,
수료 후 개인 Fork에서 별도로 수행한 가용 회차 조회 책임 분리·PreparedStatement 7회 회귀 검증만
구분해 쓴다.

## 감사 대상

| 구분 | 팀 프로젝트 결과 | 감사 관점 |
|---|---|---|
| #61 SQL/Index | 회차 조회 `123 → 83`, date/time 검색의 `shared_table` full scan 제거 | 핵심 Query·Index 구현자와 Evidence 검증 참여 분리 |
| #235 Hot-path | 회차별 반복 쿼리 배치화로 `83 → 7`, Stress p95 `13.14s → 1.34s` | K6/AWS 측정 실행자와 결과 해석·리뷰 참여 분리 |
| 개인 Fork #9 | 가용 회차 조회 책임 분리, 20개 회차 PreparedStatement 7회 유지 회귀 | 수료 후 별도 리팩토링·검증으로만 기록 |

## 원본 팀 프로젝트 근거

### 역할 표기

원본 팀 [README](https://github.com/bobfull-project/bobfull-backend/blob/develop/README.md)의 팀 소개는
조회 성능·캐시·k6를 정용태의 담당으로 적고 있다. 이 표기는 개인 Fork README에도 팀 역할로
그대로 유지돼 있다.

### #61 SQL/Index

- 원본 [Issue #61](https://github.com/bobfull-project/bobfull-backend/issues/61)과
  [PR #201](https://github.com/bobfull-project/bobfull-backend/pull/201)의 작성자는 `sighingpotato`다.
- 핵심 구현 `d625140`(회차 중복 쿼리 제거와 `shared_table(restaurant_id)` 인덱스 추가),
  측정 보강 `f0c0161`은 모두 `sighingpotato`가 작성했다.
- 김현승은 PR #201에 최소 측정 조합 누락과 단일 EXPLAIN 관측값 과장을 지적하는 MAJOR 리뷰를
  남겼다. 이후 Evidence 계약 정리·누락 EXPLAIN 시나리오 테스트를 `26e4a80`, `f9cc0c4`,
  `c86d981`로 보강했다.

### #235 Hot-path와 K6

- 원본 [Issue #235](https://github.com/bobfull-project/bobfull-backend/issues/235),
  [PR #242](https://github.com/bobfull-project/bobfull-backend/pull/242),
  [PR #249](https://github.com/bobfull-project/bobfull-backend/pull/249)의 작성자와 구현·AWS 재측정
  커밋 작성자는 `sighingpotato`다.
- 김현승의 독립 리뷰는 Load 결과만으로 "완전 해소"·"Auto Scaling 불필요"를 결론낸 것을 MAJOR로
  지적했고, 동일 Stress 조건 재측정을 요구했다. 재측정 뒤 결과는 최대 320 iter/s에서 여전히
  CPU·Hikari Pool 포화가 남는다는 조건부 결론으로 정정됐다.

## 개인 Fork와의 구분

개인 Fork의 `480aea6`(Issue #9)은 가용 회차 조회 책임을 전용 QueryService로 분리한 수료 후
리팩토링이다. 이 작업의 PreparedStatement 7회 회귀 검증은 개인이 설명 가능한 별도 결과지만,
팀 프로젝트 #61/#235의 구현·k6 실측을 개인 성과로 소급시키지 않는다.

## README·포트폴리오 표현 규칙

### 허용

- "팀 성능 개선 과정에서 SQL/EXPLAIN Evidence 검토와 누락 측정 시나리오 보강에 참여했다."
- "Load만으로 과장된 결론을 막기 위해 Stress 재측정을 요구했고, 남은 포화 한계를 조건부로 기록했다."
- "수료 후 개인 Fork에서 가용 회차 조회 책임을 분리하고 PreparedStatement 7회 회귀를 별도 검증했다."

### 금지

- "내가 SQL을 83회에서 7회로 줄였다."
- "내가 AWS K6에서 p95를 13.14초에서 1.34초로 개선했다."
- 팀의 원본 성능 측정값을 수료 후 Fork 작업의 실측 결과처럼 표시하는 표현

## 검증 한계

- 이 감사는 GitHub Issue·PR·commit·댓글과 팀 README의 역할 표기만 사용했다. 회의록, 오프라인
  협업, 별도 대화 기록은 포함하지 않았다.
- 따라서 GitHub에서 직접 확인되는 구현·측정 실행 기여와 검증·리뷰 기여만 판정 근거로 사용한다.
- README 최종 입구 구성과 포트폴리오 문구 동기화는 Issue #12에서 이 문서를 근거로 처리한다.

## 관련

- Issue: #14
- 팀 구현: #61 / #235
- 개인 Fork 리팩토링: #9
- 후속 README 정리: #12
