package com.example.incident_Root_Cause_Analyzer.service;

import com.example.incident_Root_Cause_Analyzer.domain.LogEntry;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class RagIngestionService {

    private final VectorStore vectorStore;

    public RagIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void ingestToVectorStore(LogEntry log) {
        String logContext = String.format(
                "Service: %s | Message: %s | Stacktrace: %s",
                log.getServiceName(), log.getMessage(), log.getStackTrace()
        );

        Document document = new Document(logContext, Map.of("service", log.getServiceName()));
        vectorStore.accept(List.of(document));
    }
}