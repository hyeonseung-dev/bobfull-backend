package com.bobfull.chat.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ModerationPromptOptimizationCorpusTest {

    @Test
    void issue16CorpusIsFrozenBySizeAndFingerprint() {
        List<ModerationPromptOptimizationCorpus.Case> corpus = ModerationPromptOptimizationCorpus.cases();
        String fingerprint = ModerationPromptOptimizationCorpus.fingerprint(corpus);

        assertThat(corpus).hasSize(ModerationPromptOptimizationCorpus.EXPECTED_CORPUS_SIZE);
        assertThat(fingerprint).isEqualTo(ModerationPromptOptimizationCorpus.APPROVED_FINGERPRINT);
    }
}
