-- feedback updated_by column
ALTER TABLE site_feedback ADD updated_by VARCHAR(50) NOT NULL DEFAULT 'tjs' CONSTRAINT fk_site_feedback_updated REFERENCES site_user(usercode);