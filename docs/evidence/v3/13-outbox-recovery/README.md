# Issue #13 — Outbox 유실·중복·재처리 Evidence

## 검증 대상

ChatRoom 생성 후속 처리가 핵심 결제·예약 거래와 분리돼도, `AFTER_COMMIT` signal이 실행되지 않거나 commit 뒤
프로세스가 강제 종료된 경우 DB에 남은 `OutboxEvent(PENDING)`로 다음 처리 cycle에서 복구할 수 있는지 확인한다.

## 프로젝트 당시 구현과 기존 근거

- 김현승(HS)의 `9d75f03`은 ChatRoom Transactional Outbox를 도입했고, `6b01554`/`b3a5e4f`는 retry backoff와
  scheduler 주기 정합성을 보완했다.
- 김현승의 `10a5c14`는 같은 공통 Outbox를 Email로 확장했다.
- 기존 [#176 Evidence](../176-chatroom-outbox/README.md)는 Outbox 원자 저장·rollback·PENDING 처리·멱등성·backoff·stale
  recovery를 확인한다. 기존 [#183 Evidence](../183-email-outbox/README.md)는 수신자별 `SENT` 보존을 확인한다.

## 수료 후 추가 검증

이 문서의 결정론적 signal 유실 모사, 실제 child JVM 강제 종료와 같은 MySQL DB를 이용한 새 애플리케이션 기동 복구
테스트는 Issue #13에서 추가한다. 모두 수료 후 추가 검증이며 프로젝트 당시 Evidence와 섞지 않는다.

## 측정 계약

- Primary: commit 뒤 child JVM을 강제 종료해도 핵심 거래 1건과 ChatRoom Outbox `PENDING` 1건이 보존되고, 새 child JVM의
  처리 cycle에서 ChatRoom 1건과 `COMPLETED`로 수렴한다.
- 안전 확인: 같은 event 동시 처리에도 ChatRoom은 1건이며, Email 재시도는 이미 `SENT`인 수신자를 다시 전송하지 않는다.

## 결정론적 시나리오

```text
Payment 완료 transaction
├─ Payment PAID / Reservation / Participant 저장
└─ CHAT_ROOM_CREATION_REQUESTED Outbox(PENDING) 저장
→ COMMIT
→ AFTER_COMMIT signal no-op (test-only 모사)
→ ChatRoom 0건, Outbox PENDING 유지
→ 새 Processor cycle
→ ChatRoom 1건, Outbox COMPLETED
```

signal no-op은 Production 장애 주입이 아니라 test configuration의 `ChatRoomOutboxProcessor.signal()` override다.
따라서 이 검증은 "커밋 후 signal 유실" 경계를 결정론적으로 확인하며, 실제 process kill/restart를 재현하지 않는다.

## 실제 JVM 강제 종료·재기동 시나리오

```text
child JVM 1
├─ Payment PAID / Reservation / Participant / ChatRoom Outbox(PENDING) commit
└─ AFTER_COMMIT signal 진입 직전 대기
→ parent test가 child JVM 1을 destroyForcibly
→ 동일 MySQL의 핵심 거래 1건·ChatRoom 0건·Outbox PENDING 1건 확인
→ child JVM 2가 같은 DB에서 processDueEvents 실행
→ ChatRoom 1건, Outbox COMPLETED 1건 확인
```

두 child JVM은 test configuration으로 signal과 Email dispatcher만 제어한다. 실제 MySQL 컨테이너와 별도 JVM을 사용하므로,
이 결과는 process kill/restart 경계를 검증하지만 운영 배포 환경의 모든 종료 방식이나 scheduler 경합을 뜻하지는 않는다.

## Email 기존 동작 근거

`EmailOutboxProcessorTest`는 A/C 수신자가 성공하고 B만 실패한 뒤 재처리할 때 B만 다시 SMTP 전송하는 것을 확인한다.
`EmailOutboxSignalDispatcherTest`는 executor 제출 거부 시 Processor를 호출하지 않아 `PENDING`을 scheduler가 회수할 수
있음을 확인한다.

## 실행 조건

- 실행일: 2026-09-29 KST
- 기준 branch: `test/13-outbox-recovery-evidence` (latest `develop`에서 생성)
- 결정론적 signal 유실·동시 처리·Email 검증: H2 `MODE=MySQL` Spring Boot 통합/단위 테스트. 실제 SMTP·외부 Provider는 사용하지 않았다.
- JVM 강제 종료·재기동 검증: Docker Desktop의 Testcontainers MySQL 8.4와 별도 child JVM 2개를 사용했다.
- 실행 명령:

```bash
./gradlew :test \
  --tests 'com.bobfull.payment.service.PaymentReservationConfirmationTransactionIntegrationTest' \
  --tests 'com.bobfull.chat.outbox.service.ChatRoomOutboxProcessorIntegrationTest' \
  --tests 'com.bobfull.reservation.outbox.service.EmailOutboxProcessorTest' \
  --tests 'com.bobfull.reservation.outbox.service.EmailOutboxSignalDispatcherTest' \
  --no-daemon -PshowTestOutput
```

결과: `BUILD SUCCESSFUL in 14s`.

```bash
./gradlew :test \
  --tests 'com.bobfull.payment.service.ChatRoomOutboxJvmKillRecoveryEvidenceTest' \
  --no-daemon -PshowTestOutput
```

결과: Docker Desktop/Testcontainers MySQL 8.4에서 `BUILD SUCCESSFUL in 21s`.

## 검증 결과

| 시나리오 | 결과 |
|---|---|
| signal 유실 모사 뒤 핵심 거래와 PENDING 보존 | Payment `PAID` 1건, Reservation 1건, Participant 1건, ChatRoom 0건, ChatRoom Outbox `PENDING` 1건·attempt 0 확인 |
| 새 cycle의 ChatRoom 1건 / COMPLETED 복구 | 같은 event에서 ChatRoom 1건, Outbox `COMPLETED` 1건 확인 |
| 실제 child JVM 강제 종료 뒤 재기동 복구 | MySQL에 Payment `PAID` 1건, Reservation 1건, Participant 1건, ChatRoom `0 → 1`건, ChatRoom Outbox `PENDING → COMPLETED` 1건 확인 |
| 동일 event 동시 처리의 ChatRoom 1건 | concurrent `process(eventId)` 2회 뒤 ChatRoom 1건, Outbox `COMPLETED` 1건 확인 |
| Email SENT 수신자 제외 재시도 | A/C는 각 1회, 실패한 B만 2회 SMTP 호출 확인 |
| Email executor 제출 거부 | Processor 0회 호출, `PENDING`을 scheduler 복구 대상으로 유지하는 단위 테스트 확인 |

## 남은 한계

- 실제 SMTP 호출 성공과 `SENT` DB 기록 사이에 종료되면 정확히 한 번 전송을 보장하지 않는다.
- 다중 애플리케이션 인스턴스의 장기 scheduler 경합과 운영 복구 시간은 검증하지 않는다.
