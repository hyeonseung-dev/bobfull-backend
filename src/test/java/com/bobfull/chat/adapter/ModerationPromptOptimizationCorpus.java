package com.bobfull.chat.adapter;

import com.bobfull.chat.entity.ModerationCategory;
import com.bobfull.chat.entity.ModerationResultType;
import com.bobfull.chat.entity.RiskLevel;
import com.bobfull.chat.service.ModerationRuleFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** 현재 단일 메시지 Rule에서 LLM_REQUIRED인 #251 case와 필수 경계 회귀 입력을 동결한다. */
public final class ModerationPromptOptimizationCorpus {
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

    private static Case safe(String id, String input) {
        return new Case(id, "REQUIRED_REGRESSION", input, ModerationResultType.SAFE, Set.of(), RiskLevel.LOW, false);
    }

    public record Case(String id, String type, String input, ModerationResultType expectedResult,
            Set<ModerationCategory> expectedCategories, RiskLevel expectedRisk, boolean injection) { }
}
