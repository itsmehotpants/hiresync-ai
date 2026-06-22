CREATE TABLE resume_embeddings (
    id SERIAL PRIMARY KEY,
    resume_id VARCHAR(26) NOT NULL REFERENCES resumes(id) ON DELETE CASCADE,
    user_id VARCHAR(26) NOT NULL,
    chunk_index INT NOT NULL,
    chunk_type VARCHAR(50),
    chunk_text TEXT NOT NULL,
    embedding vector(768),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
CREATE INDEX idx_resume_emb_user ON resume_embeddings(user_id);
CREATE INDEX idx_resume_emb_resume ON resume_embeddings(resume_id);
