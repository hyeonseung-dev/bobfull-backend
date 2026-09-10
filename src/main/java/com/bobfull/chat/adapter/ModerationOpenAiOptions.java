package com.bobfull.chat.adapter;

import org.springframework.ai.openai.OpenAiChatOptions;

final class ModerationOpenAiOptions {
    private ModerationOpenAiOptions() {
    }

    static OpenAiChatOptions.Builder withMaxOutputTokens(int maxOutputTokens) {
        return OpenAiChatOptions.builder().maxTokens(maxOutputTokens);
    }
}
