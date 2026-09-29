package com.bobfull.payment.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.bobfull.BobfullBackendApplication;
import com.bobfull.chat.outbox.service.ChatRoomOutboxProcessor;
import com.bobfull.chat.repository.ChatRoomRepository;
import com.bobfull.chat.service.ChatRoomCreationService;
import com.bobfull.infrastructure.outbox.repository.OutboxEventRepository;
import com.bobfull.infrastructure.outbox.service.OutboxEventTransactionService;
import com.bobfull.payment.entity.Payment;
import com.bobfull.payment.entity.PaymentPurpose;
import com.bobfull.payment.repository.PaymentRepository;
import com.bobfull.reservation.infrastructure.smtp.FakeReservationNotificationAdapter;
import com.bobfull.reservation.outbox.service.EmailOutboxProcessor;
import com.bobfull.reservation.outbox.service.EmailOutboxSignalDispatcher;
import com.bobfull.restaurant.entity.Restaurant;
import com.bobfull.restaurant.repository.RestaurantRepository;
import com.bobfull.restaurant.sharedtable.entity.SharedTable;
import com.bobfull.restaurant.sharedtable.repository.SharedTableRepository;
import com.bobfull.restaurant.timeslot.entity.TimeSlot;
import com.bobfull.restaurant.timeslot.repository.TimeSlotRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** 실제 child JVM 종료 뒤 같은 MySQL Outbox를 새 JVM이 처리하는 Issue #13 Evidence다. */
@Testcontainers(disabledWithoutDocker = true)
class ChatRoomOutboxJvmKillRecoveryEvidenceTest {

    private static final String PAYMENT_ID = "issue13-jvm-kill-payment";

    @Container
    static final GenericContainer<?> MYSQL = new GenericContainer<>("mysql:8.4")
            .withExposedPorts(3306)
            .withEnv("MYSQL_DATABASE", "issue13")
            .withEnv("MYSQL_USER", "issue13")
            .withEnv("MYSQL_PASSWORD", "issue13")
            .withEnv("MYSQL_ROOT_PASSWORD", "issue13-root-password");

    @Test
    void childJVM강제종료뒤_같은DB의_PENDING_ChatRoomOutbox를_새처리cycle이_복구한다() throws Exception {
        ChildProcess committing = startChild("commit");
        try {
            assertThat(committing.awaitLine("ISSUE13_COMMITTED_AND_BLOCKED", 60))
                    .withFailMessage("commit child JVM이 준비 지점에 도달하지 못했습니다. 출력:\n%s", committing.output())
                    .isTrue();
            assertThat(committing.destroyForcibly()).isTrue();
            assertThat(committing.waitFor(20)).isTrue();

            assertThat(queryForInt("select count(*) from payment where portone_payment_id = ? and payment_status = 'PAID'", PAYMENT_ID)).isEqualTo(1);
            assertThat(queryForInt("select count(*) from reservation")).isEqualTo(1);
            assertThat(queryForInt("select count(*) from reservation_participant")).isEqualTo(1);
            assertThat(queryForInt("select count(*) from chat_room")).isZero();
            assertThat(queryForInt("select count(*) from outbox_event where event_type = 'CHAT_ROOM_CREATION_REQUESTED' and status = 'PENDING'"))
                    .isEqualTo(1);

            ChildProcess recovering = startChild("recover");
            assertThat(recovering.waitFor(60)).isTrue();
            assertThat(recovering.output()).contains("ISSUE13_RECOVERY_COMPLETED");

            assertThat(queryForInt("select count(*) from payment where portone_payment_id = ? and payment_status = 'PAID'", PAYMENT_ID)).isEqualTo(1);
            assertThat(queryForInt("select count(*) from reservation")).isEqualTo(1);
            assertThat(queryForInt("select count(*) from reservation_participant")).isEqualTo(1);
            assertThat(queryForInt("select count(*) from chat_room")).isEqualTo(1);
            assertThat(queryForInt("select count(*) from outbox_event where event_type = 'CHAT_ROOM_CREATION_REQUESTED' and status = 'COMPLETED'"))
                    .isEqualTo(1);
        } finally {
            committing.destroyForcibly();
        }
    }

    private ChildProcess startChild(String mode) throws IOException {
        Process process = new ProcessBuilder(
                System.getProperty("java.home") + "/bin/java",
                "-cp", System.getProperty("java.class.path"),
                ChildRunner.class.getName(), mode, jdbcUrl(), "issue13", "issue13")
                .redirectErrorStream(true)
                .start();
        return new ChildProcess(process);
    }

    private int queryForInt(String sql, Object... arguments) throws Exception {
        try (java.sql.Connection connection = java.sql.DriverManager.getConnection(jdbcUrl(), "issue13", "issue13");
             java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int index = 0; index < arguments.length; index++) statement.setObject(index + 1, arguments[index]);
            try (java.sql.ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getInt(1);
            }
        }
    }

    private String jdbcUrl() {
        return "jdbc:mysql://" + MYSQL.getHost() + ":" + MYSQL.getMappedPort(3306) + "/issue13";
    }

    static final class ChildRunner {
        public static void main(String[] arguments) {
            String mode = arguments[0];
            Map<String, Object> properties = Map.ofEntries(
                    Map.entry("server.port", "0"),
                    Map.entry("spring.datasource.url", arguments[1]),
                    Map.entry("spring.datasource.username", arguments[2]),
                    Map.entry("spring.datasource.password", arguments[3]),
                    Map.entry("spring.jpa.hibernate.ddl-auto", "update"),
                    Map.entry("jwt.secret", "issue13-child-jvm-test-secret-key-with-minimum-length"),
                    Map.entry("portone.api-secret", "issue13-test-api-secret"),
                    Map.entry("portone.store-id", "issue13-test-store-id"),
                    Map.entry("portone.webhook-secret", "aXNzdWUxMy10ZXN0LXdlYmhvb2stc2VjcmV0"),
                    Map.entry("outbox.chat-room.enabled", "false"),
                    Map.entry("outbox.email.enabled", "false"),
                    Map.entry("outbox.chat-message.enabled", "false"),
                    Map.entry("payment.expiration.enabled", "false"),
                    Map.entry("payment.refund-reconciliation.enabled", "false"),
                    Map.entry("reservation.recruitment-deadline.enabled", "false"),
                    Map.entry("outbox.issue13.child-mode", mode)
            );
            try (ConfigurableApplicationContext context = new SpringApplicationBuilder(BobfullBackendApplication.class, ChildConfiguration.class)
                    .web(WebApplicationType.SERVLET).properties(properties).run()) {
                if ("commit".equals(mode)) {
                    createAndCompletePayment(context);
                } else {
                    context.getBean(ChatRoomOutboxProcessor.class).processDueEvents(10);
                    System.out.println("ISSUE13_RECOVERY_COMPLETED");
                    System.out.flush();
                }
            }
        }

        private static void createAndCompletePayment(ConfigurableApplicationContext context) {
            RestaurantRepository restaurants = context.getBean(RestaurantRepository.class);
            SharedTableRepository tables = context.getBean(SharedTableRepository.class);
            TimeSlotRepository timeSlots = context.getBean(TimeSlotRepository.class);
            PaymentRepository payments = context.getBean(PaymentRepository.class);
            Restaurant restaurant = restaurants.saveAndFlush(Restaurant.create(1L, "Issue13 식당", "제주시", "한식", "설명", "키워드", 10000));
            SharedTable table = tables.saveAndFlush(SharedTable.create(restaurant.getId(), 4));
            TimeSlot timeSlot = timeSlots.saveAndFlush(TimeSlot.create(table.getId(),
                    Instant.parse("2026-10-01T02:00:00Z"), Instant.parse("2026-10-01T04:00:00Z")));
            payments.saveAndFlush(Payment.createReady(PAYMENT_ID, 10L, timeSlot.getId(), null,
                    PaymentPurpose.CREATE, 1, BigDecimal.valueOf(10000), Instant.now().plusSeconds(3600)));
            context.getBean(PaymentCompletionTransactionService.class).complete(PAYMENT_ID, 10L);
        }
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class ChildConfiguration {
        @Bean @Primary
        FakeReservationNotificationAdapter fakeReservationNotificationAdapter() { return new FakeReservationNotificationAdapter(); }

        @Bean @Primary
        EmailOutboxSignalDispatcher noOpEmailOutboxSignalDispatcher(EmailOutboxProcessor processor) {
            return new EmailOutboxSignalDispatcher(Runnable::run, processor) {
                @Override public void dispatch(Long eventId) { }
            };
        }

        @Bean @Primary
        ChatRoomOutboxProcessor childChatRoomOutboxProcessor(OutboxEventRepository events,
                OutboxEventTransactionService transactions, ChatRoomCreationService rooms, Clock clock,
                org.springframework.core.env.Environment environment) {
            return new ChatRoomOutboxProcessor(events, transactions, rooms, clock) {
                @Override public void signal(Long eventId) {
                    if (!"commit".equals(environment.getProperty("outbox.issue13.child-mode"))) {
                        super.signal(eventId);
                        return;
                    }
                    System.out.println("ISSUE13_COMMITTED_AND_BLOCKED eventId=" + eventId);
                    System.out.flush();
                    try { Thread.sleep(TimeUnit.MINUTES.toMillis(10)); }
                    catch (InterruptedException exception) { Thread.currentThread().interrupt(); }
                }
            };
        }
    }

    private static final class ChildProcess {
        private final Process process;
        private final List<String> lines = new CopyOnWriteArrayList<>();

        private ChildProcess(Process process) {
            this.process = process;
            Thread reader = new Thread(() -> {
                try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                    for (String line; (line = input.readLine()) != null;) {
                        lines.add(line);
                    }
                } catch (IOException ignored) { }
            }, "issue13-child-output-reader");
            reader.setDaemon(true);
            reader.start();
        }

        boolean awaitLine(String fragment, int seconds) throws InterruptedException {
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(seconds);
            while (System.nanoTime() < deadline) {
                if (output().contains(fragment)) return true;
                Thread.sleep(50);
            }
            return false;
        }

        boolean destroyForcibly() { return process.isAlive() && process.destroyForcibly() != null; }
        boolean waitFor(int seconds) throws InterruptedException { return process.waitFor(seconds, TimeUnit.SECONDS); }
        String output() { return String.join("\n", lines); }
    }
}
