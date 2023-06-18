-- feedback owned_by column
ALTER TABLE site_feedback ADD owned_by VARCHAR(50) CONSTRAINT fk_site_feedback_owning_user REFERENCES site_user(usercode);