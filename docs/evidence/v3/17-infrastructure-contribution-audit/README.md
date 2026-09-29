# Issue #17 — Redis·다중 App·Blue-Green 개인 기여도 감사

## 결론

전체 판정은 **B — 협업 설계·검증 기여**다. 다만 이 결론은 인프라 전체를 HS의 직접 구현으로 묶는 뜻이 아니다. 실제 원본 PR, commit, 실행 Evidence를 기준으로 아래처럼 항목을 분리한다.

| 항목 | 판정 | 확인한 HS 기여 | 개인 성과 표현 |
|---|---|---|---|
| 채팅 Redis Pub/Sub 다중 인스턴스 전달 | A | Issue #170 / PR #230의 구현·테스트 commit 작성자. DB 커밋 후 publish, subscriber의 local STOMP 1회 전달, 2-instance 검증을 구현했다. | `다중 인스턴스 채팅을 위해 Redis Pub/Sub 전달 경로와 2-instance 회귀 검증을 구현했다.` |
| 검색 Redis Cache | B | 원본 PR #202의 구현자는 다른 팀원이다. HS의 공식 review 후 cache version invalidation·트랜잭션·Redis timeout/fail-open 회귀 항목이 보강됐다. | `검색 캐시 도입에서 동시성 및 Redis 장애 시 요청 경로 회귀를 검토했다.` |
| 공용 ElastiCache/Valkey와 다중 App 운영 구성 | B | Issue #169에서 HTTP App HA/전체 HA 구분, DB Connection Budget, Scheduler/Outbox, WSS 검증 기준을 보강했다. 실제 구성과 배포 자동화 commit의 작성자는 `gpekd5`다. | `공용 Redis를 사용하는 다중 App 구조의 정합성·검증 기준을 협업으로 보강했다.` |
| Blue-Green 자동화와 `2,787/2,787`·관측 다운타임 `0초` | C | 원본 PR #257과 Evidence의 작성자·구현 commit은 `gpekd5`다. HS가 해당 연속 요청 측정을 직접 실행했다는 commit 또는 실행 Evidence는 확인하지 못했다. | 개인 포트폴리오의 직접 구현·직접 측정 성과로 사용하지 않는다. 팀 결과로만 출처와 역할을 함께 표기할 수 있다. |

따라서 포트폴리오에는 **HS가 직접 구현한 채팅 Redis Pub/Sub**과 **협업 검토·검증 기여**만 구분해 쓴다. Blue-Green의 요청 수와 다운타임 수치는 팀 인프라 결과이지 HS 개인의 측정 결과로 전환하지 않는다.

## 감사 기준과 원본 근거

| 근거 | 확인 내용 | 감사에 사용한 범위 |
|---|---|---|
| 원본 [Issue #169](https://github.com/bobfull-project/bobfull-backend/issues/169)·[PR #257](https://github.com/bobfull-project/bobfull-backend/pull/257) | Issue/PR 작성자는 `gpekd5`. ALB, Active/Inactive Target Group, SSM 배포, Listener 전환·rollback과 실제 AWS Evidence를 담당했다. | Blue-Green 구현·운영 실측의 책임 구분 |
| 원본 PR #257의 `0ce2ab9`, `1f0df1c`, `256ba3f` 등 | Blue-Green 자동화와 App HA Evidence commit의 작성자는 `gpekd5`다. | HS 직접 구현 주장 배제 |
| 원본 Issue #169의 HS 댓글 | App 계층 HA와 전체 HA의 구분, DB Connection Budget, 다중 인스턴스 Scheduler/Outbox, WSS 검증 조건을 보강했다. | 설계·검증 B 근거 |
| 원본 [Issue #170](https://github.com/bobfull-project/bobfull-backend/issues/170)·[PR #230](https://github.com/bobfull-project/bobfull-backend/pull/230) | HS가 `e90edaf`, `bcd31dd`, `eceb749`, `44d547a`, `8de1b43` 등을 작성했다. | Redis Pub/Sub 직접 구현·검증 A 근거 |
| 원본 [PR #202](https://github.com/bobfull-project/bobfull-backend/pull/202) | 구현자는 `sighingpotato`; HS review 뒤 `a6a681a`, `2ab96fd`에서 invalidation/transaction/fail-open 검토 사항을 보완했다. | 검색 Cache 협업 검토 B 근거 |
| 현재 [Issue #169 Evidence](../169-app-ha/README.md), [Issue #170 Evidence](../170-chat-redis-pubsub/README.md), [ADR 0014](../../../adr/0014-shared-redis-elasticache.md) | 운영 결과와 각 기능의 Redis 장애·복구 한계를 교차 확인했다. | 수치와 실패 경계의 의미 제한 |

`2,787/2,787 HTTP 200`, 실패 `0`, 관측 다운타임 `0초`는 PR #257의 public readiness 연속 요청 결과다. 이는 해당 측정 구간에서 Application Layer traffic switch 중 실패가 관측되지 않았다는 뜻이며, 전체 시스템의 HA 또는 모든 장애 유형에서의 무중단을 뜻하지 않는다.

## 개인 포트폴리오 문구 경계

### 사용할 수 있는 표현

- `Redis Pub/Sub 기반 다중 인스턴스 채팅 전달 경로를 구현하고, DB 메시지 단일 저장과 인스턴스별 1회 전달을 2-instance 테스트로 검증했다.`
- `검색 Redis Cache 도입에서 cache invalidation 경쟁과 Redis 장애 시 fail-open 회귀 위험을 코드 review로 점검했다.`
- `다중 App 전환 시 HTTP App HA와 전체 시스템 HA를 구분하고, Connection Budget·Scheduler/Outbox·WSS 검증 조건을 협업으로 보강했다.`
- `팀의 ALB Blue-Green 배포 검증 결과를 검토할 때, Application Layer 범위와 Single-AZ RDS·단일 Kafka broker 한계를 함께 기록했다.`

### 사용하면 안 되는 표현

- `Blue-Green을 직접 구축하고 2,787건 무중단 배포를 검증했다.`
- `배포 다운타임을 0초로 만들었다.`
- `Redis/Valkey를 고가용성으로 구성했다.`
- `다중 App 구성으로 전체 서비스 SPOF를 제거했다.`
- `검색 캐시를 구현해 Redis 장애에도 무중단을 보장했다.`

현재 최상위 README는 Blue-Green과 다중 App의 담당을 김홍기로 표기하고, `2,787/2,787`을 팀의 Application Layer 검증 결과로 표기하며, RDS Single-AZ·Kafka 단일 broker 한계도 명시한다. 별도 개인 PROFILE/portfolio 파일은 저장소에서 찾지 못했으므로 이번 감사에서는 기존 README를 수정하지 않는다.

## 실패 경계와 복구 경로

| 의존성 또는 상황 | 확인된 동작 | 복구 또는 제한 |
|---|---|---|
| Redis Access Token Blacklist 조회 실패 | 기존 인증 흐름을 계속 진행하는 fail-open 정책 | 보안 정책상 허용한 범위의 가용성 우선이며, Redis 자체 HA를 뜻하지 않는다. |
| Refresh Token Redis 조회 실패 | 새 토큰을 발급하지 않는다. | fail-closed 성격이며 재로그인 또는 Redis 복구가 필요하다. |
| 검색 Cache Redis timeout/오류 | DB 조회 경로가 요청을 계속 처리하도록 검토·회귀 보강했다. | DB가 추가 부하를 감당해야 하며, DB 장애까지 복구하지 않는다. |
| 채팅 Redis Pub/Sub publish/subscribe 단절 | 이미 DB에 커밋된 ChatMessage는 롤백하지 않고 실시간 전달이 누락될 수 있다. | Pub/Sub은 durable queue가 아니다. 클라이언트는 DB cursor 조회로 놓친 메시지를 다시 가져온다. |
| App EC2 한 대 장애 | ALB가 Healthy target으로 HTTP 요청을 우회하는 범위를 검증했다. | WebSocket 세션·DB·Redis·Kafka 장애까지 포함한 전체 서비스 복구 보장은 아니다. |
| Green 전환 뒤 public 검증 실패 | Listener weight를 이전 Target Group으로 rollback하는 흐름을 검증했다. | DB schema 비호환, 공유 인프라 장애, 이미 발생한 외부 side effect를 자동으로 되돌린다는 뜻은 아니다. |

## 남아 있는 SPOF와 비주장 범위

- RDS는 Single-AZ이므로 DB 계층 장애는 ALB의 App target 전환으로 해결되지 않는다.
- Kafka는 단일 EC2·단일 KRaft broker 구성으로, broker 장애 HA가 검증 대상이 아니다.
- 공용 ElastiCache/Valkey를 사용하지만 Redis/Valkey 장애 HA·복제·클러스터 failover는 이 감사에서 직접 검증하지 않았다.
- Redis Pub/Sub은 메시지 저장·재생을 제공하지 않는다. 단절 구간 실시간 메시지 유실은 DB cursor 경로로만 보완한다.
- `2,787/2,787`은 특정 public readiness endpoint의 약 6분 측정 결과다. 장기 장애 전환, DB 장애, Kafka 장애, 모든 API·WebSocket의 무중단 보장을 나타내지 않는다.
- 과거 팀 결과를 현재 개인 브랜치의 새 테스트로 재현하거나 개인 성과 수치로 환산하지 않았다.

## 검증과 Output

- 원본 Issue/PR/commit 작성자, 현재 Evidence, ADR, README의 역할 표기를 대조했다.
- 실행 코드·인프라 설정을 변경하지 않았으므로 애플리케이션 테스트와 전체 build는 이번 문서 감사 범위에서 실행하지 않는다.
- Troubleshooting 기록: `NO` — 신규 장애 재현·원인 분석이 아니라 과거 기여도와 표현 범위를 감사했다.
- Tech Blog: `NO` — 새로운 기술 결과가 없다.
- Interview Note: `YES` — A/B/C 구분, fail-open/fail-closed, Pub/Sub 유실과 DB cursor 복구를 설명 대상으로 남긴다.
- Portfolio Candidate: `YES` — 단, 위의 사용할 수 있는 표현 범위로만 사용한다.
