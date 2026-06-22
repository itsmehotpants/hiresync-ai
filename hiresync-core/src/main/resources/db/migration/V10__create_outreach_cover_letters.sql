CREATE TABLE outreach_messages (
    id VARCHAR(26) PRIMARY KEY,
    user_id VARCHAR(26) NOT NULL,
    contact_id VARCHAR(26) REFERENCES contacts(id),
    job_application_id VARCHAR(26) REFERENCES job_applications(id),
    channel VARCHAR(50),
    subject VARCHAR(255),
    body TEXT,
    sent_at TIMESTAMP WITH TIME ZONE,
    response_received_at TIMESTAMP WITH TIME ZONE,
    status VARCHAR(50) DEFAULT 'DRAFT',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE cover_letters (
    id VARCHAR(26) PRIMARY KEY,
    job_application_id VARCHAR(26) REFERENCES job_applications(id) ON DELETE CASCADE,
    user_id VARCHAR(26) NOT NULL,
    content TEXT NOT NULL,
    tone VARCHAR(50),
    version INT DEFAULT 1,
    is_final BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
CREATE INDEX idx_outreach_user ON outreach_messages(user_id);
CREATE INDEX idx_cover_letters_job ON cover_letters(job_application_id);
