package com.ssafy.home.chatbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("txt", "md", "pdf");
    private static final TokenTextSplitter TEXT_SPLITTER = new TokenTextSplitter();

    private final VectorStore vectorStore;

    public void ingest(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");
        }
        validateExtension(file.getOriginalFilename());

        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        TikaDocumentReader reader = new TikaDocumentReader(resource);
        List<Document> docs = reader.get();

        List<Document> chunks = TEXT_SPLITTER.apply(docs);

        vectorStore.add(chunks);
    }

    private void validateExtension(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
        }
        int dotIdx = filename.lastIndexOf('.');
        String ext = (dotIdx >= 0) ? filename.substring(dotIdx + 1).toLowerCase() : "";
        if (!SUPPORTED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다: " + ext);
        }
    }
}
