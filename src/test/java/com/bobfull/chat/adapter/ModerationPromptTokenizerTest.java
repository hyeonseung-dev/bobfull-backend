package com.bobfull.chat.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class ModerationPromptTokenizerTest {
    @Test
    void gpt_4o_o200k_tokenizer로_A_B_C_System_Prompt_감소율을_측정한다() {
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        Encoding encoding = registry.getEncodingForModel(ModelType.GPT_4O);
        Map<String, Integer> tokens = ModerationPromptCandidates.all().stream()
                .collect(Collectors.toMap(ModerationPromptCandidates.Candidate::id,
                        candidate -> encoding.countTokens(candidate.systemPrompt())));

        int current = tokens.get("A_CURRENT");
        int compressed = tokens.get("B_COMPRESSED");
        int minimal = tokens.get("C_MINIMAL");
        System.out.printf("[ISSUE16-PROMPT-TOKENS] encoder=o200k_base A=%d B=%d C=%d B_reduction=%.2f%% C_reduction=%.2f%%%n",
                current, compressed, minimal, reduction(current, compressed), reduction(current, minimal));

        assertThat(current).isPositive();
        assertThat(compressed).isLessThan(current);
        assertThat(minimal).isLessThan(compressed);
    }

    private static double reduction(int baseline, int candidate) {
        return (baseline - candidate) * 100.0 / baseline;
    }
}
