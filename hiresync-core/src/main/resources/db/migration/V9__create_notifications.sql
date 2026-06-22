CREATE TABLE notifications (
    id VARCHAR(26) PRIMARY KEY,
    user_id VARCHAR(26) NOT NULL,
    title VARCHAR(255) NOT NULL,
    body TEXT,
    notification_type VARCHAR(100),
    is_read BOOLEAN DEFAULT FALSE,
    related_entity_id VARCHAR(26),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_user_unread ON notifications(user_id, is_read);
