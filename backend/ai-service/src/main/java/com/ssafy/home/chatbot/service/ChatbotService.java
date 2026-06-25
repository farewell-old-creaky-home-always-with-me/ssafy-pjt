package com.ssafy.home.chatbot.service;

import com.ssafy.home.chatbot.dto.ChatResponse;
import com.ssafy.home.chatbot.dto.SearchResponse;
import com.ssafy.home.chatbot.prompt.ChatbotPromptProvider;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ChatbotPromptProvider promptProvider;

    public ChatResponse chat(String question) {
        List<Document> docs;
        try {
            docs = vectorStore.similaritySearch(
                SearchRequest.builder().query(question).topK(4).build()
            );
        } catch (Exception e) {
            log.warn("Vector store search failed, falling back to non-RAG: {}", e.getMessage());
            docs = List.of();
        }
        boolean ragUsed = !docs.isEmpty();

        String answer;
        if (ragUsed) {
            String context = docs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));
            answer = chatClient.prompt()
                .system(promptProvider.ragSystemPrompt(context))
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
