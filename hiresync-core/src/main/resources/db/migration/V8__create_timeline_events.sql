CREATE TABLE timeline_events (
    id VARCHAR(26) PRIMARY KEY,
    job_application_id VARCHAR(26) NOT NULL REFERENCES job_applications(id) ON DELETE CASCADE,
    event_type VARCHAR(100) NOT NULL,
    description TEXT,
    occurred_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
CREATE INDEX idx_timeline_job_id ON timeline_events(job_application_id);
