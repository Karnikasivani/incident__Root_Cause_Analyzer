package com.example.incident_Root_Cause_Analyzer.controller;

import com.example.incident_Root_Cause_Analyzer.domain.LogEntry;
import com.example.incident_Root_Cause_Analyzer.dto.IngestionResponse;
import com.example.incident_Root_Cause_Analyzer.service.IngestionService;
import com.example.incident_Root_Cause_Analyzer.service.RagIngestionService;
import com.example.incident_Root_Cause_Analyzer.service.IncidentAgentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ingestion")
public class IngestionController {

    private final IngestionService ingestionService;
    private final RagIngestionService ragIngestionService;
    private final IncidentAgentService incidentAgentService;

    public IngestionController(IngestionService ingestionService,
                               RagIngestionService ragIngestionService,
                               IncidentAgentService incidentAgentService) {
        this.ingestionService = ingestionService;
        this.ragIngestionService = ragIngestionService;
        this.incidentAgentService = incidentAgentService;
    }

    @PostMapping("/logs")
    public ResponseEntity<IngestionResponse> processLogs(@RequestBody LogEntry logEntry) {
        // 1. Process standard SQL data storage via existing service
        ingestionService.saveLog(logEntry);

        // 2. Load data onto the vector pipeline
        ragIngestionService.ingestToVectorStore(logEntry);

        // 3. Evaluate context vector spacing via Advisor Agents
        String prompt = "Analyze immediate failure behaviors for: " + logEntry.getServiceName();
        String reportOutput = incidentAgentService.analyzeIncidentWithAgent(prompt);

        return ResponseEntity.ok(new IngestionResponse("SUCCESS", reportOutput));
    }
}