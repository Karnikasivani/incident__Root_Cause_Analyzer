package com.example.incident_Root_Cause_Analyzer.controller;

import com.example.incident_Root_Cause_Analyzer.dto.Ingestionrequest.BulkIngestRequest;
import com.example.incident_Root_Cause_Analyzer.dto.IngestionResponse;
import com.example.incident_Root_Cause_Analyzer.service.IngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ingestion")
@RequiredArgsConstructor
@Tag(name = "Ingestion", description = "Ingest logs, alerts, and metrics from distributed services")
public class Ingestioncontroller {
        private final IngestionService ingestionService;


        @PostMapping("/logs")
        @Operation(
                summary = "Ingest a batch of log entries",
                description = "Accepts logs from one or more services, creates a correlated Incident, and queues it for AI analysis."
        )
        public ResponseEntity<IngestionResponse> ingestLogs(@Valid @RequestBody BulkIngestRequest request) {
            IngestionResponse response = ingestionService.ingest(request);
            return ResponseEntity.ok(response);
        }
}

