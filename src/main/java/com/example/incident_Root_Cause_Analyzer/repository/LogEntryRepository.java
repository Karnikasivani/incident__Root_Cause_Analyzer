package com.example.incident_Root_Cause_Analyzer.repository;

import com.example.incident_Root_Cause_Analyzer.domain.LogEntry;
import com.example.incident_Root_Cause_Analyzer.domain.LogLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;


public interface LogEntryRepository extends JpaRepository<LogEntry, String>{
    List<LogEntry> findByIncidentId(String incidentId);

    List<LogEntry> findByTraceId(String traceId);

    /** Fetch all logs for a service within a time window — used for correlation. */
    List<LogEntry> findByServiceNameAndTimestampBetweenOrderByTimestampAsc(
            String serviceName, Instant from, Instant to);

    /** Fetch ERROR/FATAL logs within a window — starting point for incident detection. */
    @Query("""
            SELECT l FROM LogEntry l
            WHERE l.timestamp BETWEEN :from AND :to
            AND l.level IN :levels
            ORDER BY l.timestamp ASC
            """)
    List<LogEntry> findByTimestampBetweenAndLevelIn(
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("levels") List<LogLevel> levels);

    long countByIncidentIdAndLevel(String incidentId, LogLevel level);
}
