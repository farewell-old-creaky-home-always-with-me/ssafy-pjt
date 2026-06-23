package com.ssafy.home.chatbot.service;

import com.ssafy.home.chatbot.dto.ChatResponse;
import com.ssafy.home.chatbot.dto.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public ChatResponse chat(String question) {
        List<Document> docs = vectorStore.similaritySearch(
            SearchRequest.builder().query(question).topK(4).build()
        );
        boolean ragUsed = !docs.isEmpty();

        String answer;
        if (ragUsed) {
            String context = docs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));
            String system = """
                당신은 부동산 정보 도우미입니다. 아래 참고 문서를 바탕으로 답변하세요.
                참고 문서에 없는 내용은 '제공된 문서에 해당 정보가 없습니다'라고 안내하세요.

                [참고 문서]
                %s
                """.formatted(context);
            answer = chatClient.prompt()
                .system(system)
                .user(question)
                .call()
                .content();
        } else {
            answer = chatClient.prompt()
                .user(question)
                .call()
                .content();
        }

        return new ChatResponse(answer, ragUsed);
    }

    public List<SearchResponse> search(String query, int topK) {
        return vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(topK).build()
            ).stream()
            .map(doc -> new SearchResponse(doc.getText(), doc.getMetadata()))
            .toList();
    }
}
