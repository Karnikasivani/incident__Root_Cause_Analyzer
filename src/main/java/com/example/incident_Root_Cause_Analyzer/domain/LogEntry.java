package com.example.incident_Root_Cause_Analyzer.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_entries", indexes = {
        @Index(name = "idx_log_service_timestamp", columnList = "service_name, timestamp"),
        @Index(name = "idx_log_trace_id", columnList = "trace_id"),
        @Index(name = "idx_log_incident_id", columnList = "incident_id"),
        @Index(name = "idx_log_level", columnList = "log_level")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** The microservice that emitted this log. */
    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    /** When the log was emitted at the source (not ingestion time). */
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "log_level", nullable = false, length = 10)
    private LogLevel level;

    /** The actual log message text — this is what gets embedded for RAG. */
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    /** Distributed trace ID — links correlated logs across services. */
    @Column(name = "trace_id", length = 64)
    private String traceId;

    /** Span ID within the trace. */
    @Column(name = "span_id", length = 64)
    private String spanId;

    /** Host/pod/container that emitted this log. */
    @Column(name = "host", length = 200)
    private String host;

    /**
     * Grouping key: which incident does this log belong to?
     * Null until the correlation step runs.
     */
    @Column(name = "incident_id")
    private String incidentId;

    /** When we ingested this log (server-side). */
    @Column(name = "ingested_at", nullable = false, updatable = false)
    private Instant ingestedAt;

    @PrePersist
    void prePersist() {
        this.ingestedAt = Instant.now();
    }

    @Lob                  // Tells Hibernate to store this as a Large Object (CLOB) for long logs
    private String stackTrace;    // <-- Matches logEntry.getStackTrace()

    private LocalDateTime timestamp1;

    // Default timestamp initialization if not provided in JSON payload
    public LogEntry() {
        this.timestamp1 = LocalDateTime.now();
    }
}