package com.example.incident_Root_Cause_Analyzer.dto;

import com.example.incident_Root_Cause_Analyzer.domain.LogLevel;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.List;


public class Ingestionrequest {
    @Data
    public static class LogEntryRequest {

        @NotBlank(message = "serviceName is required")
        private String serviceName;

        /** ISO-8601 timestamp from the source system. */
        @NotNull(message = "timestamp is required")
        private Instant timestamp;

        @NotNull(message = "level is required")
        private LogLevel level;

        @NotBlank(message = "message is required")
        private String message;

        /** Optional — present if the service emits distributed traces. */
        private String traceId;
        private String spanId;
        private String host;
    }

    @Data
    public static class BulkIngestRequest {

        @NotEmpty(message = "logs list must not be empty")
        @Valid
        private List<LogEntryRequest> logs;

        /**
         * Optional: caller can hint a time window for correlation.
         * If absent, the service derives it from the log timestamps.
         */
        private String incidentHint; // free-text label, e.g. "payment-service outage 2025-05"
    }
}
