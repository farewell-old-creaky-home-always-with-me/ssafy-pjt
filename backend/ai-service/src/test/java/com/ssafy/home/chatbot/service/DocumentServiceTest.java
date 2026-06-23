package com.ssafy.home.chatbot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private VectorStore vectorStore;

    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        documentService = new DocumentService(vectorStore);
    }

    @Test
    void txt_파일을_업로드하면_VectorStore에_저장된다() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.txt", "text/plain",
            "Spring AI를 활용한 RAG 테스트 문서입니다.".getBytes()
        );

        documentService.ingest(file);

        verify(vectorStore).add(anyList());
    }

    @Test
    void 지원하지_않는_확장자는_IllegalArgumentException을_던진다() {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.exe", "application/octet-stream",
            "binary".getBytes()
        );

        assertThatThrownBy(() -> documentService.ingest(file))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("지원하지 않는 파일 형식");
    }

    @Test
    void 파일명이_null이면_IllegalArgumentException을_던진다() {
        MockMultipartFile file = new MockMultipartFile(
            "file", null, "text/plain", "content".getBytes()
        );

        assertThatThrownBy(() -> documentService.ingest(file))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
