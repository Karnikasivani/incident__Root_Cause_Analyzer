package com.example.incident_Root_Cause_Analyzer.controller;


import com.example.incident_Root_Cause_Analyzer.domain.Incident;
import com.example.incident_Root_Cause_Analyzer.domain.IncidentStatus;
import com.example.incident_Root_Cause_Analyzer.domain.LogEntry;
import com.example.incident_Root_Cause_Analyzer.repository.IncidentRepository;
import com.example.incident_Root_Cause_Analyzer.repository.LogEntryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
@Tag(name = "Incidents", description = "Query and manage incidents")
public class Incidentcontroller {
    private final IncidentRepository incidentRepository;
    private final LogEntryRepository logEntryRepository;

    @GetMapping
    @Operation(summary = "List all incidents", description = "Returns all incidents, newest first.")
    public List<Incident> listAll() {
        return incidentRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get incident by ID")
    public ResponseEntity<Incident> getById(@PathVariable String id) {
        return incidentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/logs")
    @Operation(summary = "Get all logs belonging to an incident")
    public ResponseEntity<List<LogEntry>> getLogs(@PathVariable String id) {
        if (!incidentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(logEntryRepository.findByIncidentId(id));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "List incidents by status")
    public List<Incident> byStatus(@PathVariable IncidentStatus status) {
        return incidentRepository.findByStatusOrderByCreatedAtDesc(status);
    }
}
