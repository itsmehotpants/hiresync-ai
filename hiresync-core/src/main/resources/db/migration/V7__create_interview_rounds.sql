CREATE TABLE interview_rounds (
    id VARCHAR(26) PRIMARY KEY,
    job_application_id VARCHAR(26) NOT NULL REFERENCES job_applications(id) ON DELETE CASCADE,
    round_number INT NOT NULL,
    round_type VARCHAR(100),
    interviewer_name VARCHAR(255),
    interviewer_role VARCHAR(255),
    scheduled_at TIMESTAMP WITH TIME ZONE,
    duration_minutes INT,
    questions_asked TEXT,
    my_answers TEXT,
    feedback_received TEXT,
    self_rating INT CHECK (self_rating BETWEEN 1 AND 5),
    outcome VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
CREATE INDEX idx_interview_rounds_job ON interview_rounds(job_application_id);
CREATE INDEX idx_interview_rounds_scheduled ON interview_rounds(scheduled_at);
