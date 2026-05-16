package com.example.incident_Root_Cause_Analyzer.repository;

import com.example.incident_Root_Cause_Analyzer.domain.Incident;
import com.example.incident_Root_Cause_Analyzer.domain.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, String> {
    List<Incident> findByStatusOrderByCreatedAtDesc(IncidentStatus status);
    List<Incident> findByAffectedServicesContainingIgnoreCase(String serviceName);
}
