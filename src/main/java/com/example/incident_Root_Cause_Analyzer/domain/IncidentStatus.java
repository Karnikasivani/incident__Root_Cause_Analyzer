package com.example.incident_Root_Cause_Analyzer.domain;

public enum IncidentStatus {
    OPEN,
    ANALYZING,
    PENDING_REVIEW,  // human-in-the-loop checkpoint (bonus)
    RESOLVED,
    CLOSED
}
