package com.example.incident_Root_Cause_Analyzer.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class IncidentAgentService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public IncidentAgentService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.build();
    }

    public String analyzeIncidentWithAgent(String traceSummary) {
        return this.chatClient.mutate()
                .defaultSystem("""
                    You are an expert production engineering analysis advisor. 
                    Ground your response strictly using the retrieved historical context logs.
                    Provide a concise Root Cause Analysis and an immediate hotfix suggestion.
                """)
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder()
                        .topK(3)
                        .similarityThreshold(0.7)
                        .build()))
                .build()
                .prompt()
                .user(traceSummary)
                .call()
                .content();
    }
}