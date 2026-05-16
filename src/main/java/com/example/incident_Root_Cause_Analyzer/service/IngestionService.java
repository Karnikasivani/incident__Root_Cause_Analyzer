package com.example.incident_Root_Cause_Analyzer.service;

import com.example.incident_Root_Cause_Analyzer.domain.*;
import com.example.incident_Root_Cause_Analyzer.dto.Ingestionrequest.BulkIngestRequest;
import com.example.incident_Root_Cause_Analyzer.dto.Ingestionrequest.LogEntryRequest;
import com.example.incident_Root_Cause_Analyzer.dto.IngestionResponse;
import com.example.incident_Root_Cause_Analyzer.repository.IncidentRepository;
import com.example.incident_Root_Cause_Analyzer.repository.LogEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IngestionService {

    private final LogEntryRepository logEntryRepository;
    private final IncidentRepository incidentRepository;

    @Transactional
    public IngestionResponse ingest(BulkIngestRequest request) {
        return null;
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private LogEntry toEntity(LogEntryRequest req) {
        return LogEntry.builder()
                .serviceName(req.getServiceName())
                .timestamp(req.getTimestamp())
                .level(req.getLevel())
                .message(req.getMessage())
                .traceId(req.getTraceId())
                .spanId(req.getSpanId())
                .host(req.getHost())
                .build();
    }

    /**
     * Severity ladder mirrors PagerDuty conventions:
     * P1 = any FATAL | P2 = ERROR | P3 = WARN | P4 = INFO/DEBUG/TRACE
     */
    private String deriveSeverity(List<LogEntry> entries) {
        Set<LogLevel> levels = entries.stream()
                .map(LogEntry::getLevel)
                .collect(Collectors.toSet());

        if (levels.contains(LogLevel.FATAL)) return "P1";
        if (levels.contains(LogLevel.ERROR)) return "P2";
        if (levels.contains(LogLevel.WARN))  return "P3";
        return "P4";
    }

    private String buildIncidentTitle(String hint, Set<String> services, String severity) {
        if (hint != null && !hint.isBlank()) {
            return hint;
        }
        String serviceLabel = services.size() == 1
                ? services.iterator().next()
                : services.size() + " services";
        return severity + " incident — " + serviceLabel + " @ " + Instant.now();
    }

    public void saveLog(LogEntry logEntry) {
    }
}