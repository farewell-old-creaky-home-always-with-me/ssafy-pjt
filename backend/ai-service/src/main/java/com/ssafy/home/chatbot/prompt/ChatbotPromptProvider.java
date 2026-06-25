package com.ssafy.home.chatbot.prompt;

import org.springframework.stereotype.Component;

@Component
public class ChatbotPromptProvider {

    public String ragSystemPrompt(String context) {
        return """
            당신은 부동산 정보 도우미입니다. 아래 참고 문서를 바탕으로 답변하세요.
            참고 문서에 없는 내용은 '제공된 문서에 해당 정보가 없습니다'라고 안내하세요.

            [참고 문서]
            %s
            """.formatted(context);
    }
}
