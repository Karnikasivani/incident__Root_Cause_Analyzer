package com.example.incident_Root_Cause_Analyzer.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * An Incident groups correlated log entries that likely share a root cause.
 *
 * Lifecycle:
 *   OPEN       → ingested, awaiting analysis
 *   ANALYZING  → agent pipeline is running (Phase 3)
 *   PENDING_REVIEW → human-in-the-loop checkpoint hit (bonus feature)
 *   RESOLVED   → root cause identified and summary generated
 *
 * The rootCause and summary fields are populated by the AI layer in Phase 3.
 * affectedServices is a denormalized list for quick display.
 */
@Entity
@Table(name = "incidents", indexes = {
        @Index(name = "idx_incident_status", columnList = "status"),
        @Index(name = "idx_incident_window", columnList = "window_start, window_end")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** Human-readable title derived during ingestion (e.g. "payment-service ERROR spike"). */
    @Column(name = "title", nullable = false)
    private String title;

    /** Start of the time window covering the correlated logs. */
    @Column(name = "window_start", nullable = false)
    private Instant windowStart;

    /** End of the time window. */
    @Column(name = "window_end", nullable = false)
    private Instant windowEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private IncidentStatus status = IncidentStatus.OPEN;

    /**
     * Services involved in this incident.
     * Stored as comma-separated string for simplicity — not worth a join table here.
     */
    @Column(name = "affected_services", columnDefinition = "TEXT")
    private String affectedServices;

    /** Populated by the AI agent in Phase 3. */
    @Column(name = "root_cause", columnDefinition = "TEXT")
    private String rootCause;

    /** Human-readable AI-generated summary populated in Phase 3/4. */
    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    /** Recommended actions from the AI. */
    @Column(name = "recommended_actions", columnDefinition = "TEXT")
    private String recommendedActions;

    /** Severity: P1 (critical) → P4 (low). Set during ingestion based on log levels. */
    @Column(name = "severity", length = 5)
    private String severity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now();
    }
}