CREATE TABLE job_applications (
    id VARCHAR(26) PRIMARY KEY,
    user_id VARCHAR(26) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company_name VARCHAR(255) NOT NULL,
    role_title VARCHAR(255) NOT NULL,
    job_url TEXT,
    job_description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'WISHLIST',
    stage_order INT DEFAULT 0,
    source VARCHAR(100),
    location VARCHAR(255),
    is_remote BOOLEAN DEFAULT FALSE,
    salary_min BIGINT,
    salary_max BIGINT,
    salary_currency VARCHAR(10) DEFAULT 'INR',
    notes TEXT,
    applied_at DATE,
    deadline_at DATE,
    ai_match_score INT,
    ai_feedback TEXT,
    ats_prediction VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
CREATE INDEX idx_job_apps_user_id ON job_applications(user_id);
CREATE INDEX idx_job_apps_status ON job_applications(status);
CREATE INDEX idx_job_apps_user_status ON job_applications(user_id, status);
