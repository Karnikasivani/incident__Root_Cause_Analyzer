package com.example.incident_Root_Cause_Analyzer.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class IngestionResponse {

    /** How many log entries were persisted. */
    private int logsIngested;

    /** The incident ID created or updated by this ingestion batch. */
    private String incidentId;

    /** Derived severity based on log levels in the batch. */
    private String severity;

    /** Services detected in this batch. */
    private List<String> affectedServices;

    /** Message to display in Swagger/Postman. */
    private String message;
}

