# To-Be 도메인 경계와 책임 설계

> Issue #7. 실제 Java 이동·import 수정은 하지 않는다. Issue #5·#6 As-Is 분석과 현재 코드의 다음 리팩토링 입력이다.

## 결론

현재 최상위 패키지는 **도메인(reservation), 하위 자원(sharedtable), 전달 방식(outbox/kafka), 외부 기술(notification), 기술 공통(common)** 이 섞여 있다. 따라서 패키지명만 보고 “무슨 비즈니스이며 주인이 누구인가”를 일관되게 답하기 어렵다. Human READY 결정에 따라 핵심 도메인을 최상위에 유지하고, 공통 전달 엔진과 외부 SDK를 `infrastructure`로 모으되, 각 도메인의 후속 작업 정책은 해당 도메인에 남기는 구조를 최종 To-Be로 확정한다.

## 1. 현재 최상위 패키지 분류

| 현재 | 분류 | 판단 |
|---|---|---|
| reservation | 핵심 비즈니스 도메인 | 예약·참여·좌석·모집 상태 소유 |
| payment | 핵심 비즈니스 도메인 | READY/PAID/EXPIRED/REFUNDED와 PG 검증 소유 |
| restaurant | 핵심 비즈니스 도메인 | 식당 소유권·보증금·검색 소유 |
| member, auth | 핵심 도메인 / 인증 하위 기능 | Member 상태와 인증 수단은 분리 가능하나 함께 이해돼야 함 |
| chat | 핵심 비즈니스 도메인 | 채팅방·메시지·접근·신고 상태 소유 |
| restaurantinsight | 하위 도메인 | 식당 피드백 파생 결과를 소유, Kafka는 입력 방식 |
| sharedtable, timeslot | restaurant 하위 도메인 | 식당의 테이블·회차 자원이며 독립 사업 능력 아님 |
| admin | Application orchestration | 여러 도메인 읽기 projection과 운영 행위 |
| outbox | 메시징/비동기 처리 인프라 + 공통 엔진 | 상태 기계는 공통, 실제 event 정책은 도메인별 |
| kafka | 외부 시스템 Adapter | Consumer/Retry/DLT는 전달 기술이며 비즈니스 주인이 아님 |
| notification | 외부 시스템 Adapter | SMTP는 전달 수단; 알림 의미는 reservation이 소유 |
| common | 기술 공통 기능 | 응답·예외·보안·transaction hook. 도메인 event 이름의 집적은 관찰 후보 |

**판정:** 한 가지 원칙으로 설명되지 않는다. To-Be에서는 “상태·규칙의 주인”을 최상위 기준으로, transport/SDK/shared engine은 `infrastructure` 기준으로 통일한다.

## 2. 도메인 책임 재정의

| 영역 | 소유 상태·규칙 | 제공 기능 | 받아야 할 정보 | 현재 내부 구현 직접 인지 후보 |
|---|---|---|---|---|
| reservation | Reservation/Participant, 정원·모집·취소 접수·좌석 | 준비, 확정, 취소 완료 | 회차 정원/시각, Payment 완료·환불 완료 | payment service, restaurant/table/slot Repository |
| payment | Payment/Refund, READY·PG 검증·환불·재조정 | READY 생성, 결제/환불 완료 | reservation 대상/확정·취소 완료 | reservation 구현 service adapter |
| refund | Payment 내부 하위 책임 | Refund 요청·상태·reconcile | participant/payment 연결 | reservation completion service |
| settlement | Payment 내부 읽기 책임 | 지급 예정 금액 조회 | 식당 소유권·회차·예약 projection | 다수 Repository 직접 조합 |
| restaurant | Restaurant·OWNER 소유권·보증금 | 식당/이미지/검색 | Member ID | image service와 table/slot 사용 정보 |
| sharedtable | 식당의 테이블·정원 | 테이블 관리 | Restaurant 소유권, reservation 사용 여부 | RestaurantRepository 직접 참조 |
| timeslot | 테이블의 시간 회차 | 회차 관리·예약 가능 정보 | table/restaurant/예약·READY 사용 여부 | 여러 타 도메인 Repository |
| member/auth | Member·역할 / token 수단 | 인증 주체·회원 정보 | 없음 | auth가 MemberRepository 직접 참조(자연스러운 하위 기능) |
| chat | ChatRoom/Message/Moderation/Report·접근 | 메시지·방·검수 | reservation access, member name | outbox Repository와 reservation adapter |
| restaurant insight | Insight/Item·익명 집계 규칙 | 메시지 분석·OWNER 조회 | chat event, restaurant 역추적 | chat→reservation→slot→table→restaurant Repository |
| admin | 운영 조회 projection | 목록·통계·관리 action | 각 도메인 read model | 여러 Repository 직접 조회(읽기 전용이면 허용 가능) |

## 3. 기술 요소의 소유권

| 클래스/기술 | 현재 | To-Be 제안 | 이유 |
|---|---|---|---|
| `RestaurantFeedbackInsightConsumer` | kafka | `restaurantinsight/infrastructure/kafka` | insight가 목적, Kafka는 입력 adapter |
| `ChatMessageOutboxProcessor` | outbox | chat 후속 정책 + 공통 engine 사용 | event payload·완료 기준은 chat 소유 |
| `PortOnePaymentReader` | payment adapter | `payment/infrastructure/portone` | Payment의 외부 결제 adapter |
| `EmailOutboxProcessor`/SMTP | outbox/notification | 공통 delivery engine + `reservation` notification policy | SMTP는 수단, 예약 결과가 의미 소유 |
| Redis Pub/Sub | chat realtime | `chat/infrastructure/redis` | 채팅 실시간 전달 목적 |
| AI Provider | chat/restaurantinsight adapter | 각 도메인 `infrastructure/ai` | moderation/insight 규칙은 서로 다름 |

### Outbox의 분리 판단

`OutboxEvent`, claim, retry, stale recovery, scheduler, 상태 관리는 **공통 outbox engine**으로 남기는 편이 낫다. 반면 ChatRoom 생성, 이메일 문구/대상, Kafka event payload/ACK 완료 조건은 **도메인별 후속 작업**이다. 둘을 같은 주인으로 둘 필요는 없으며, engine은 공통 infrastructure, event handler/policy는 발생 도메인에 둔다. 이는 “모든 outbox를 흩뜨리자”는 제안이 아니다.

## 4. 양방향 의존 판단

| 관계 | 계약 소유 / 자연 호출 | 유지할 경계 | To-Be 판단 |
|---|---|---|---|
| reservation ↔ payment | reservation은 READY/환불 요청, payment는 완료 사실을 reservation에 전달 | `ReservationCancellationRefundPort`, `ReservationConfirmationPort` | 구현 service를 상대 패키지에 직접 노출하지 말고 완료/요청 계약과 adapter를 명확히 배치. 양방향 import만으로 결함 확정 금지. |
| chat ↔ outbox | chat은 메시지/방 후속 의도, outbox는 전달 보장 | ChatRoom의 `createIfAbsent`, 동일 거래 outbox 저장 | engine과 handler를 분리해 chat이 outbox entity/repository 세부를 덜 알도록 검토. 원자성은 유지. |

## 5. 절대 보존 조건

- READY Payment의 10분 임시 좌석 선점, CREATE TimeSlot 및 JOIN Reservation→TimeSlot 비관적 락.
- Payment 행 잠금·상태/만료 재확인과 Payment/Reservation 확정의 하나의 transaction.
- PortOne 외부 호출은 긴 DB transaction 밖, 취소 접수와 환불 완료는 분리.
- `CANCEL_REQUESTED`는 좌석 점유, 거래 상태와 Outbox 저장은 원자적.
- ChatRoom/Email/Kafka 실패는 핵심 거래를 rollback하지 않고 재처리 가능해야 한다.

## 6. To-Be 후보 비교

| 후보 | 구조 가독성 | 이동량/테스트 영향 | 위험·적합성 |
|---|---|---|---|
| A. 기능별 최소 정리 | 최상위 명칭만 정리, 낮음 | 낮음 | infrastructure 주인을 여전히 흐릴 수 있음 |
| B. 모든 도메인 내부 4계층 | 일관성 높음 | 매우 큼 | 현 규모에는 과도한 추상화·대규모 이동 위험 |
| C. 도메인 중심 + 선택적 Port/Adapter 강화 **추천** | 주인과 외부 기술 구분 명확 | 단계적 중간 | 거래 핵심부터 이동 금지, 신입 포트폴리오 설명과 향후 모듈 분리에 적절 |

## 7. 확정 To-Be 패키지 구조

```text
com.bobfull
├── reservation        # 예약·참여·좌석·노쇼
├── payment            # payment/refund/settlement, PortOne adapter
├── restaurant         # restaurant/sharedtable/timeslot/image
├── member             # member/auth
├── chat               # room/message/moderation, Redis adapter
├── restaurantinsight  # insight와 Kafka/AI input adapter
├── admin              # 운영 projection
├── infrastructure
│   ├── outbox-engine  # event 상태, claim/retry/stale recovery
│   ├── kafka          # 공통 consumer/error support
│   └── notification   # SMTP transport
└── common             # response, exception, security, clock, transaction support
```

Human READY 결정에 따라 `refund`와 `settlement`는 `payment` 내부 책임으로 확정한다. `infrastructure/outbox-engine`은 공통 상태·재시도 엔진이며, 실제 ChatRoom·Email·Kafka 후속 handler는 각 도메인의 책임으로 둔다. 도메인 내부는 처음부터 `application/domain/infrastructure/presentation`으로 일괄 분해하지 않고, payment·chat·insight처럼 Port/Adapter 가치가 확인된 지점부터 선택적으로 적용한다.

## 8. Issue #8 실제 이동 계획

| 단위 | 현재 → 목표 | 영향 import·테스트 | 거래 보호 조건 |
|---|---|---|---|
| 8-1 | `payment.adapter.PortOne*` → `payment.infrastructure.portone` | payment port/config, PG mock·완료/웹훅 테스트 | 외부 호출은 Payment 잠금 transaction 밖 유지 |
| 8-2 | `kafka.RestaurantFeedbackInsightConsumer` → `restaurantinsight.infrastructure.kafka` | event DTO, listener config, consumer retry/DLT 테스트 | Insight의 메시지 역추적·독립 consumer group 유지 |
| 8-3 | `sharedtable`,`timeslot` → `restaurant` 하위 모듈 | reservation target/capacity Reader, OWNER CRUD·예약 준비 회귀 | CREATE TimeSlot, JOIN Reservation→TimeSlot 락 순서 유지 |
| 8-4 | `outbox` engine ↔ 도메인 handler 분리 | reservation/chat/email/kafka imports, claim/retry/stale·rollback 회귀 | 거래와 Outbox 저장 원자성, 후속 실패 비롤백 유지 |
| 8-5 | payment 하위의 refund/settlement 책임 명시 | admin/조회 import, 지급 예정 금액·환불 상태 테스트 | PAID-완료 Refund 계산 및 환불 완료 순서 유지 |

각 단위는 별도 commit/검증으로 진행하며, 8-4는 다른 단위의 handler 이동을 전제로 하지 않는다. 실제 이동 전에는 기준 Head에서 import 목록과 관련 단위·통합 테스트를 다시 수집한다.

## 9. As-Is 문서와의 정합성

Issue #5의 reservation↔payment 및 chat↔outbox import 양방향 관찰과 충돌하지 않는다. 본 설계는 양방향 자체를 결함으로 단정하지 않고, 계약·handler·engine의 소유권을 명확히 하는 후속 범위를 정한다. Issue #6의 READY 선점, 락 순서, Payment/Reservation 원자성, 외부 PG 호출 위치, 취소/환불 분리, Outbox 원자성은 §5의 변경 금지 조건으로 유지한다.
