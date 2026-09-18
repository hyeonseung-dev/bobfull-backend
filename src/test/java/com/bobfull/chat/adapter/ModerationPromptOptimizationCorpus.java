package com.bobfull.chat.adapter;

import com.bobfull.chat.entity.ModerationCategory;
import com.bobfull.chat.entity.ModerationResultType;
import com.bobfull.chat.entity.RiskLevel;
import com.bobfull.chat.service.ModerationRuleFilter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** 현재 단일 메시지 Rule에서 LLM_REQUIRED인 #251 case와 필수 경계 회귀 입력을 동결한다. */
public final class ModerationPromptOptimizationCorpus {
    public static final int EXPECTED_CORPUS_SIZE = 43;
    public static final String APPROVED_FINGERPRINT = "2e74ca9da5ce4d735d3bbfe45472d70a5564f8d1d25dcc265e8963f68602ce6e";

    private ModerationPromptOptimizationCorpus() { }

    public static List<Case> cases() {
        ModerationRuleFilter ruleFilter = new ModerationRuleFilter();
        List<Case> corpus = new ArrayList<>();
        for (Issue251HardeningDataset.SingleMessageCase source : Issue251HardeningDataset.singleMessageCases()) {
            if (ruleFilter.clearFlagged(source.input()).isEmpty()) {
                corpus.add(new Case(source.caseId(), source.type(), source.input(), source.proposedModerationResult(),
                        source.proposedCategories(), source.proposedRisk(), source.type().equals("PROMPT_INJECTION")));
            }
        }
        corpus.add(safe("FRAGMENT-01", "죽"));
        corpus.add(safe("FRAGMENT-02", "010"));
        corpus.add(safe("FRAGMENT-03", "시"));
        corpus.add(safe("FRAGMENT-04", "간"));
        corpus.add(safe("BOUNDARY-01", "식당 대표번호는 02-1234-5678입니다"));
        corpus.add(safe("BOUNDARY-02", "맛집 지도 링크입니다 https://map.example"));
        corpus.add(safe("BOUNDARY-03", "바보야"));
        return List.copyOf(corpus);
    }

    public static String fingerprint(List<Case> corpus) {
        StringBuilder serialized = new StringBuilder();
        for (Case testCase : corpus) {
            append(serialized, testCase.id());
            append(serialized, testCase.type());
            append(serialized, testCase.input());
            append(serialized, testCase.expectedResult().name());
            append(serialized, testCase.expectedCategories().stream()
                    .map(Enum::name)
                    .sorted()
                    .collect(Collectors.joining(",")));
            append(serialized, testCase.expectedRisk().name());
            append(serialized, Boolean.toString(testCase.injection()));
        }
        return sha256(serialized.toString());
    }

    private static void append(StringBuilder serialized, String value) {
        serialized.append(value.length()).append(':').append(value).append('\n');
    }

    private static String sha256(String value) {
        try {
            return java.util.HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm is unavailable", exception);
        }
    }

    private static Case safe(String id, String input) {
        return new Case(id, "REQUIRED_REGRESSION", input, ModerationResultType.SAFE, Set.of(), RiskLevel.LOW, false);
    }

    public record Case(String id, String type, String input, ModerationResultType expectedResult,
            Set<ModerationCategory> expectedCategories, RiskLevel expectedRisk, boolean injection) { }
}
