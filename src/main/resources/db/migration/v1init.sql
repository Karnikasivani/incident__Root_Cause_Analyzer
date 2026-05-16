-- V1__init.sql
-- Enable pgvector extension (required for Phase 2 embeddings)
-- This runs once when the app first boots.
CREATE EXTENSION IF NOT EXISTS vector;

-- Incidents table
CREATE TABLE IF NOT EXISTS incidents (
    id                VARCHAR(36)  PRIMARY KEY,
    title             TEXT         NOT NULL,
    window_start      TIMESTAMPTZ  NOT NULL,
    window_end        TIMESTAMPTZ  NOT NULL,
    status            VARCHAR(20)  NOT NULL DEFAULT 'OPEN',
    affected_services TEXT,
    root_cause        TEXT,
    summary           TEXT,
    recommended_actions TEXT,
    severity          VARCHAR(5),
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_incidents_status ON incidents(status);
CREATE INDEX IF NOT EXISTS idx_incidents_window ON incidents(window_start, window_end);

-- Log entries table
CREATE TABLE IF NOT EXISTS log_entries (
    id            VARCHAR(36)  PRIMARY KEY,
    service_name  VARCHAR(100) NOT NULL,
    timestamp     TIMESTAMPTZ  NOT NULL,
    log_level     VARCHAR(10)  NOT NULL,
    message       TEXT         NOT NULL,
    trace_id      VARCHAR(64),
    span_id       VARCHAR(64),
    host          VARCHAR(200),
    incident_id   VARCHAR(36),
    ingested_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_log_service_timestamp ON log_entries(service_name, timestamp);
CREATE INDEX IF NOT EXISTS idx_log_trace_id          ON log_entries(trace_id);
CREATE INDEX IF NOT EXISTS idx_log_incident_id       ON log_entries(incident_id);
CREATE INDEX IF NOT EXISTS idx_log_level             ON log_entries(log_level);

-- Phase 2: pgvector table for log embeddings (created now, populated later)
-- 1536 dims = OpenAI text-embedding-3-small / Spring AI default
-- Adjust to 1024 if using Anthropic embeddings
CREATE TABLE IF NOT EXISTS log_embeddings (
    id          VARCHAR(36)   PRIMARY KEY,
    log_entry_id VARCHAR(36)  NOT NULL REFERENCES log_entries(id) ON DELETE CASCADE,
    content     TEXT          NOT NULL,  -- the text that was embedded
    embedding   vector(1536),            -- the actual embedding vector
    metadata    JSONB,                   -- service, level, timestamp for filtering
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- HNSW index for fast approximate nearest-neighbour search (pgvector 0.5+)
-- cosine distance works best for text embeddings
CREATE INDEX IF NOT EXISTS idx_log_embeddings_hnsw
    ON log_embeddings USING hnsw (embedding vector_cosine_ops);