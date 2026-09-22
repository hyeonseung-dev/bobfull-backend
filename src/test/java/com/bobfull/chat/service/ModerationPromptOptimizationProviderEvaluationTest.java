package com.bobfull.chat.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.bobfull.chat.adapter.ModerationPromptCandidates;
import com.bobfull.chat.adapter.ModerationPromptOptimizationCorpus;
import com.bobfull.chat.dto.AiModerationResponse;
import com.bobfull.chat.dto.ModerationResult;
import com.bobfull.chat.entity.ModerationCategory;
import com.bobfull.chat.entity.ModerationResultType;
import com.bobfull.chat.entity.RiskLevel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/** Issue #16의 Prompt 후보 비교 전용 opt-in Provider 평가다. */
@Tag("openai-evaluation")
@EnabledIfEnvironmentVariable(named = "ISSUE16_PROMPT_EVALUATION", matches = "true")
@ActiveProfiles("local")
@SpringBootTest(properties = {
        "spring.ai.openai.chat.model=${OPENAI_EVAL_MODEL:${OPENAI_CHAT_MODEL:gpt-4o-mini}}",
        "spring.datasource.url=jdbc:h2:mem:issue16-prompt-evaluation;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa", "spring.datasource.password=", "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop", "jwt.secret=openai-evaluation-only-secret-key-with-minimum-length",
        "portone.api-secret=test-api-secret", "portone.store-id=test-store-id", "portone.webhook-secret=d2hzZWNfZEdWemRDMXpkR055WlhRPQ==",
        "spring.mail.host=localhost", "spring.mail.port=1025", "payment.expiration.enabled=false",
        "payment.refund-reconciliation.enabled=false", "reservation.recruitment-deadline.enabled=false",
        "reservation.dining-end.enabled=false", "outbox.chat-room.enabled=false", "outbox.email.enabled=false"
})
class ModerationPromptOptimizationProviderEvaluationTest {
    private static final String EVALUATION_EVIDENCE_VERSION = "issue16-prompt-v2";
    private static final int REPEAT_RUNS = Integer.parseInt(System.getenv().getOrDefault("ISSUE16_PROMPT_REPEAT_RUNS", "1"));

    @Autowired @Qualifier("moderationChatClient") private ChatClient moderationChatClient;
    @Value("${spring.ai.openai.chat.model}") private String configuredModel;
    @Value("${bobfull.ai.moderation.max-output-tokens}") private int maxOutputTokens;

    @DynamicPropertySource
    static void openAiApiKey(DynamicPropertyRegistry registry) {
        registry.add("spring.ai.openai.api-key", () -> System.getenv("OPENAI_API_KEY"));
    }

    @Test
    void A_B_C_동일_LLM_corpus를_Structured_Output과_Validator로_비교한다() {
        List<ModerationPromptCandidates.Candidate> candidates = selectedCandidates();
        List<ModerationPromptOptimizationCorpus.Case> corpus = ModerationPromptOptimizationCorpus.cases();
        assertThat(corpus).extracting(ModerationPromptOptimizationCorpus.Case::id).doesNotHaveDuplicates();
        System.out.printf("[ISSUE16-CONTRACT] version=%s corpus=%d candidates=%s configuredModel=%s maxOutputTokens=%d repeats=%d%n",
                EVALUATION_EVIDENCE_VERSION, corpus.size(), candidates.stream().map(ModerationPromptCandidates.Candidate::id).toList(), configuredModel, maxOutputTokens, REPEAT_RUNS);

        for (int run = 1; run <= REPEAT_RUNS; run++) {
            for (ModerationPromptCandidates.Candidate candidate : candidates) {
                Metrics metrics = evaluate(candidate, corpus);
                metrics.print(candidate.id(), run, configuredModel);
                assertThat(metrics.providerFailures).as("Provider 호출 실패").isZero();
            }
        }
    }

    private List<ModerationPromptCandidates.Candidate> selectedCandidates() {
        Map<String, ModerationPromptCandidates.Candidate> candidates = ModerationPromptCandidates.all().stream()
                .collect(Collectors.toMap(ModerationPromptCandidates.Candidate::id, candidate -> candidate));
        String selection = System.getenv("ISSUE16_PROMPT_CANDIDATES");
        if (selection == null || selection.isBlank()) return ModerationPromptCandidates.all();
        return java.util.Arrays.stream(selection.split(",")).map(String::trim).map(candidates::get)
                .peek(candidate -> { if (candidate == null) throw new IllegalArgumentException("Unknown candidate"); }).toList();
    }

    private Metrics evaluate(ModerationPromptCandidates.Candidate candidate, List<ModerationPromptOptimizationCorpus.Case> corpus) {
        Metrics metrics = new Metrics();
        for (ModerationPromptOptimizationCorpus.Case testCase : corpus) {
            long startedAt = System.nanoTime();
            try {
                ResponseEntity<ChatResponse, ModerationResult> response = moderationChatClient.prompt().system(candidate.systemPrompt())
                        .user(testCase.input()).options(OpenAiChatOptions.builder().maxTokens(maxOutputTokens))
                        .call().responseEntity(ModerationResult.class, spec -> spec.useProviderStructuredOutput());
                Observation observation = toObservation(response);
                long latencyMs = elapsedMillis(startedAt);
                boolean validatorPassed = metrics.add(testCase, observation.response(), latencyMs);
                System.out.printf("[ISSUE16-PROMPT-RAW] candidate=%s case=%s expected=%s/%s/%s raw=%s actual=%s/%s/%s validator=%s promptTokens=%s completionTokens=%s totalTokens=%s latencyMs=%d%n",
                        candidate.id(), testCase.id(), testCase.expectedResult(), testCase.expectedCategories(), testCase.expectedRisk(),
                        observation.raw().replace("\\n", "\\\\n"), observation.response().result().result(), observation.response().result().categories(),
                        observation.response().result().riskLevel(), validatorPassed ? "PASS" : "FAIL", observation.response().promptTokens(),
                        observation.response().completionTokens(), observation.response().totalTokens(), latencyMs);
            } catch (RuntimeException exception) {
                metrics.providerFailures++;
                metrics.latencies.add(elapsedMillis(startedAt));
                System.out.printf("[ISSUE16-PROMPT-FAILURE] candidate=%s case=%s failure=%s%n", candidate.id(), testCase.id(), exception.getClass().getSimpleName());
            }
        }
        return metrics;
    }

    private Observation toObservation(ResponseEntity<ChatResponse, ModerationResult> response) {
        ChatResponseMetadata metadata = response.response().getMetadata();
        Usage usage = metadata == null ? null : metadata.getUsage();
        AiModerationResponse result = new AiModerationResponse(response.entity(), "OpenAI", metadata == null || metadata.getModel() == null ? configuredModel : metadata.getModel(),
                usage == null ? null : asLong(usage.getPromptTokens()), usage == null ? null : asLong(usage.getCompletionTokens()),
                usage == null ? null : asLong(usage.getTotalTokens()));
        return new Observation(result, response.response().getResult().getOutput().getText());
    }

    private static long elapsedMillis(long startedAt) { return (System.nanoTime() - startedAt) / 1_000_000; }
    private static Long asLong(Integer value) { return value == null ? null : value.longValue(); }
    private record Observation(AiModerationResponse response, String raw) { }

    private static final class Metrics {
        int total; int resultExact; int categoryExact; int riskExact; int fp; int fn; int schemaFailures; int validatorFailures; int providerFailures;
        int injectionPass; int injectionDetermined; int fragmentPass; int fragmentTotal;
        long promptTokens; long completionTokens; long totalTokens; int tokenMeasuredCalls;
        final List<Long> latencies = new ArrayList<>();

        boolean add(ModerationPromptOptimizationCorpus.Case expected, AiModerationResponse response, long latencyMs) {
            ModerationResult actual = response.result();
            total++; latencies.add(latencyMs);
            if (response.promptTokens() != null && response.completionTokens() != null && response.totalTokens() != null) {
                tokenMeasuredCalls++;
                promptTokens += response.promptTokens();
                completionTokens += response.completionTokens();
                totalTokens += response.totalTokens();
            }
            if (actual == null || actual.result() == null || actual.categories() == null || actual.riskLevel() == null) { schemaFailures++; return false; }
            boolean validatorPassed = true;
            try { ModerationResultValidator.validate(actual); } catch (ModerationAnalysisException exception) { validatorFailures++; validatorPassed = false; }
            resultExact += expected.expectedResult() == actual.result() ? 1 : 0;
            categoryExact += expected.expectedCategories().equals(actual.categories()) ? 1 : 0;
            riskExact += expected.expectedRisk() == actual.riskLevel() ? 1 : 0;
            if (expected.expectedResult() == ModerationResultType.SAFE && actual.result() == ModerationResultType.FLAGGED) fp++;
            if (expected.expectedResult() == ModerationResultType.FLAGGED && actual.result() == ModerationResultType.SAFE) fn++;
            if (expected.injection() && expected.expectedResult() == ModerationResultType.FLAGGED) { injectionDetermined++; if (actual.result() == ModerationResultType.FLAGGED) injectionPass++; }
            if (expected.id().startsWith("FRAGMENT-") || expected.id().startsWith("BOUNDARY-")) { fragmentTotal++; if (actual.result() == expected.expectedResult() && actual.categories().equals(expected.expectedCategories())) fragmentPass++; }
            return validatorPassed;
        }

        void print(String candidate, int run, String model) {
            double averageInput = tokenMeasuredCalls == 0 ? 0 : (double) promptTokens / tokenMeasuredCalls;
            double accuracy = total == 0 ? 0 : (double) resultExact / total;
            double latency = latencies.stream().mapToLong(Long::longValue).average().orElse(0);
            System.out.printf(Locale.ROOT,
                    "[ISSUE16-PROMPT-SUMMARY] candidate=%s run=%d actualModel=%s cases=%d accuracy=%d/%d(%.3f) categoryExact=%d/%d riskExact=%d/%d FP=%d FN=%d schemaFailures=%d validatorFailures=%d providerFailures=%d injectionGate=%d/%d fragmentGate=%d/%d promptTokens=%d completionTokens=%d totalTokens=%d avgInputTokens=%.2f latencyAvgMs=%.1f%n",
                    candidate, run, model, total, resultExact, total, accuracy, categoryExact, total, riskExact, total, fp, fn, schemaFailures,
                    validatorFailures, providerFailures, injectionPass, injectionDetermined, fragmentPass, fragmentTotal, promptTokens, completionTokens,
                    totalTokens, averageInput, latency);
        }
    }
}
