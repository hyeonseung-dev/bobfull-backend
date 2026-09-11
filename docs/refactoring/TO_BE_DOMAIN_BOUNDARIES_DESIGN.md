# To-Be 도메인 경계와 책임 설계

> Issue #7. 이 문서는 Java 패키지·import·의존성을 변경하지 않는 Issue #8의 설계 입력이다. 판단 근거는 Issue #5·#6 As-Is 분석과 현재 `src/main/java` 코드다.

## 결론

BobFull은 하나의 Spring Boot 모놀리스다. 따라서 최상위 패키지는 사용하는 기술이 아니라 **사용자가 인식하는 비즈니스 기능과 그 상태·규칙의 주인**을 먼저 보여야 한다. Kafka, Redis, PortOne, SMTP는 각 기능이 목적을 수행하기 위한 구현 세부다. 공통 Outbox engine만 예외적으로 여러 기능의 신뢰성 메커니즘이므로 공통 기술 영역에 남긴다.

## 1. 현재 문제 → 사례 공통점 → BobFull 기준

| 단계 | 확인 내용 | BobFull 적용 |
|---|---|---|
| 현재 문제 | `sharedtable`, `timeslot` 같은 하위 자원과 `kafka`, `notification`, `outbox` 같은 전달·기술 패키지가 도메인과 같은 최상위에 있다. | 첫 화면에서 예약·결제·식당·회원·채팅·피드백·관리 기능을 먼저 읽을 수 없고, 클래스의 주인을 역추적해야 한다. |
| 실제 서비스 사례에서 확인한 공통점 | 모놀리스 패키지는 대체로 업무 기능을 먼저 보이고, 외부 SDK·메시지 브로커는 그 기능 안의 구현으로 숨긴다. 공통 코드는 실제 공통 책임이 있을 때만 별도 위치를 둔다. | “기술을 쓴다”가 아니라 “무엇을 하기 위해 이 기술을 쓰는가”로 위치를 정한다. |
| BobFull 기준 | 최상위는 상태·규칙·API 의미의 소유자다. 기술 adapter는 해당 기능의 내부 `infrastructure`에 둔다. | 일괄 계층화 없이, 외부 경계가 실제로 있는 payment·chat·restaurantinsight부터 선택적으로 내부 구분을 적용한다. |

`infrastructure/kafka`를 미리 만들지 않는다. 여러 기능이 실제로 공유하는 serializer, 공통 ErrorHandler, 범용 Kafka 설정처럼 **순수 공통 기술 코드가 확인될 때만** 별도 공통 위치를 검토한다.

## 2. 기능별 책임과 최상위 분류

| 최상위 기능 | 소유 상태·규칙 | 다른 기능에 제공하는 정보/기능 | 내부 기술 구현 후보 |
|---|---|---|---|
| reservation | Reservation/Participant, 정원·모집·취소 접수·좌석 | 예약 준비·확정·취소 완료, 예약 결과 알림 의도 | SMTP 알림 adapter, 예약 후속 Outbox handler |
| payment | Payment/Refund, READY·PG 검증·환불·지급 예정 금액 | READY 생성, 결제/환불 완료, 지급 예정 금액 조회 | PortOne adapter |
| restaurant | Restaurant, SharedTable, TimeSlot, 이미지·OWNER 규칙 | 식당·테이블·회차·예약 가능 정보 | 이미지 저장 adapter |
| member | Member·역할·인증 주체 | 인증된 회원과 권한 정보 | token/auth 구현 |
| chat | ChatRoom/Message/Moderation/Report·접근 | 채팅방·메시지·검수 | Redis realtime, AI moderation, Kafka input/output adapter |
| restaurantinsight | Insight/Item·익명 피드백 집계 | 식당 피드백 분석 결과 | Kafka consumer, AI 분석 adapter |
| admin | 운영 조회 projection과 운영 행위 | 여러 기능의 관리 화면용 읽기 모델 | 필요 시 각 기능 Reader 사용 |
| common | response·예외·보안·clock·transaction support | 기술 공통 지원 | 도메인 규칙이나 특정 event 정책은 두지 않음 |

`refund`와 `settlement`는 별도 최상위 기능이 아니다. refund는 Payment 상태와 생명주기가 연결되고, settlement는 현재 `PAID Payment`에서 `COMPLETED Refund`를 차감한 지급 예정 금액 **조회**다. 실제 송금, 정산 주기·수수료·계좌, 정산 실패·재지급 같은 독립 상태와 정책이 도입될 때에만 분리를 재검토한다.

## 3. 기술 구현의 소유권

| 현재 위치·클래스 | 최종 목표 위치 | 판단 |
|---|---|---|
| `payment.adapter.PortOneSdkPaymentReader`, `PortOneSdkWebhookVerifier`, `PortOneRefundGatewayAdapter`, `PortOneConfig` | `payment.infrastructure.portone` | Payment의 PG 조회·검증·환불 구현이다. |
| `kafka.consumer.RestaurantFeedbackInsightConsumer`, `RestaurantInsightDltRecoverer`, `RestaurantInsightConsumerConfig` | `restaurantinsight.infrastructure.kafka` | 식당 피드백 분석이 목적이고 Kafka는 입력 방식이다. |
| `kafka.consumer.ChatModerationConsumer`, `ChatModerationDltRecoverer`, chat moderation Kafka config | `chat.infrastructure.kafka` | 채팅 검수가 목적이고 Kafka는 비동기 전달 방식이다. |
| `chat.realtime.Redis*` | `chat.infrastructure.redis` | 실시간 채팅 전달 구현이다. |
| `chat.adapter.SpringAiModerationAdapter` | `chat.infrastructure.ai` | 채팅 검수의 외부 AI 구현이다. |
| `restaurantinsight.adapter.SpringAiRestaurantFeedbackInsightAdapter` | `restaurantinsight.infrastructure.ai` | 식당 피드백 분석의 외부 AI 구현이다. |
| `notification.adapter.SmtpReservationNotificationAdapter` | `reservation.infrastructure.smtp` | 현재 `ReservationNotificationPort`의 구현이며, 수신자·시점·문구 의미는 예약 결과가 결정한다. |

notification은 독립 비즈니스 도메인이 아니다. `ReservationNotificationService`, `EmailOutboxEventService`가 표현하는 “예약 결과를 누구에게 언제 알릴지”는 reservation이 소유하고, SMTP는 그 결정을 전송하는 adapter다.

## 4. Outbox: 공통 engine과 기능별 후속 작업

| 구분 | 소유 | 대상 | 기준 |
|---|---|---|---|
| 공통 engine | `infrastructure.outbox` | `OutboxEvent`, 상태, claim, retry, stale recovery, scheduler, 공통 처리 transaction | 실패했을 때 어떻게 claim·재시도·복구하는가 |
| 채팅 후속 작업 | `chat` | ChatRoom 생성, 채팅 메시지 Kafka 발행의 event 의미·payload·완료 기준 | 무엇을 해야 하는가 |
| 예약 후속 작업 | `reservation` | 예약 결과 이메일 생성·수신자·문구·예약 event 정책 | 무엇을 해야 하는가 |
| 식당 피드백 후속 작업 | `restaurantinsight` | chat message event를 입력으로 분석하는 Kafka handler | 무엇을 해야 하는가 |

따라서 `ChatRoomOutboxProcessor`, `ChatMessageOutboxProcessor`, `EmailOutboxProcessor`는 공통 engine 자체가 아니라 각 기능의 handler로 분리하는 것이 목표다. handler는 engine의 claim/retry 계약을 사용하되, 핵심 거래와 `OutboxEvent` 저장의 원자성 및 handler 실패가 거래를 rollback하지 않는 성질은 유지한다.

## 5. 최종 To-Be 패키지 구조

```text
com.bobfull
├── reservation                  # 예약·참여·좌석·취소·예약 알림 정책
│   └── infrastructure
│       └── smtp                 # ReservationNotificationPort 구현
├── payment                      # 결제·refund·settlement(지급 예정 금액 조회)
│   └── infrastructure
│       └── portone
├── restaurant                   # 식당·sharedtable·timeslot·이미지
├── member                       # 회원·인증
├── chat                         # 채팅방·메시지·검수·신고
│   └── infrastructure
│       ├── redis
│       ├── kafka
│       └── ai
├── restaurantinsight            # 식당 피드백 분석
│   └── infrastructure
│       ├── kafka
│       └── ai
├── admin                        # 운영 projection과 관리 기능
├── infrastructure
│   └── outbox                   # 공통 상태·claim·retry·stale recovery·scheduler
└── common                       # response·exception·security·clock·transaction support
```

이 tree는 주요 기능을 먼저 보이게 하며, `infrastructure`는 공통 Outbox engine처럼 실제로 공통인 책임에만 사용한다. 모든 도메인을 `application/domain/infrastructure/presentation` 네 계층으로 일괄 분해하지 않는다.

## 6. 유지할 경계와 거래 보호 조건

| 관계 | 유지할 경계 | 변경 금지 조건 |
|---|---|---|
| reservation ↔ payment | `ReservationCancellationRefundPort`, `ReservationConfirmationPort` | READY Payment 10분 좌석 선점, CREATE TimeSlot 및 JOIN Reservation→TimeSlot 비관적 락, Payment 잠금·상태 재확인, Payment/Reservation 확정 원자성 |
| payment ↔ PortOne | `PortOnePaymentReader`, `PortOneWebhookVerifier`, `PortOneRefundRequester` | 외부 PG 호출을 긴 DB transaction 안에 넣지 않음. 취소 접수와 실제 환불 완료 분리 |
| 기능 ↔ Outbox | 거래 안의 Event 저장과 engine의 처리 계약 | `CANCEL_REQUESTED` 좌석 점유, 핵심 거래와 Outbox 저장 원자성, handler 실패 비롤백 |
| chat / restaurantinsight ↔ Kafka·Redis·AI | 각 기능 Port/adapter | 기술 변경이 Chat/Insight 비즈니스 규칙으로 새지 않게 함 |

Issue #5의 reservation↔payment 및 chat↔outbox import 양방향 관찰은 결함 확정 근거가 아니다. Issue #8은 구현 클래스를 상대 기능에 노출하는 정도를 줄이고 계약 소유를 분명히 하되, 위 거래 규칙을 보존하는 범위에서만 이동한다.

## 7. Issue #8 실제 이동 계획

| 순서 | 현재 → 목표 | 영향 import·테스트 | 거래·락 영향 |
|---|---|---|---|
| 8-1 | payment의 `PortOne*` adapter/config → `payment.infrastructure.portone` | payment port bean wiring, PG mock, 결제 완료·웹훅·환불 테스트 | 외부 호출의 transaction 밖 위치와 Payment 락 순서 유지 |
| 8-2 | restaurantinsight 전용 Kafka consumer/DLT/config → `restaurantinsight.infrastructure.kafka` | listener factory, event DTO, consumer retry/DLT 테스트 | Insight의 독립 consumer group·실패 처리 유지 |
| 8-3 | chat 전용 Kafka consumer/DLT/config 및 Redis/AI 구현 → `chat.infrastructure.kafka`·`redis`·`ai` | listener·Redis pub/sub·moderation mock 테스트 | ChatMessage 저장과 비동기 검수·발행 경계 유지 |
| 8-4 | `notification.adapter.SmtpReservationNotificationAdapter` 및 예약 이메일 handler → `reservation.infrastructure.smtp`와 reservation handler | `ReservationNotificationPort`, 이메일 delivery·예약 생성/참여/취소 이메일 테스트 | 이메일 실패가 예약·결제 거래를 rollback하지 않음 |
| 8-5 | `outbox`의 공통 engine과 chat/reservation/insight handler 분리 | event type별 claim/retry/stale recovery, handler rollback 회귀 | 핵심 거래+Outbox 저장 원자성, handler 재시도·복구 보장 |
| 8-6 | `sharedtable`, `timeslot` → restaurant 하위 패키지 | reservation capacity/target Reader, OWNER CRUD·예약 준비 회귀 | CREATE TimeSlot 및 JOIN Reservation→TimeSlot 락 순서 유지 |
| 8-7 | payment 안에서 refund/settlement 하위 책임을 명시 | admin 조회 import, 지급 예정 금액·환불 상태 테스트 | `PAID - COMPLETED Refund` 계산과 환불 완료 순서 유지 |

각 단위는 별도 commit과 관련 단위·통합 테스트로 검증한다. 실제 이동 직전 기준 Head에서 import 목록과 Spring component scan 영향을 다시 수집하며, 8-5는 다른 handler 이동과 한 commit으로 묶지 않는다.

## 8. As-Is 문서와의 정합성

Issue #5의 패키지·직접 참조 분석 및 Issue #6의 거래 흐름과 충돌하지 않는다. 이 설계는 기술을 공통 최상위로 승격하는 대신 기능 주인 아래에 배치하는 방향이며, Outbox의 공통 신뢰성 메커니즘만 별도 공통 기술 책임으로 유지한다. 코드 이동과 import 변경은 Issue #8에서만 수행한다.
