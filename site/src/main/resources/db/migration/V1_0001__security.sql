-- USER

CREATE TABLE site_user (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    old_id BIGINT,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL,
    owner_id BIGINT NOT NULL,
    usercode VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    salt VARCHAR(10) NOT NULL,
    password_version VARCHAR(1) NOT NULL,
    access_level VARCHAR(1) NOT NULL DEFAULT 'M'
);

CREATE UNIQUE INDEX idx_siteuser_usercode ON site_user(usercode);

INSERT INTO site_user (updated_by_id, owner_id, usercode, password, email, salt, password_version, access_level) VALUES (1, 1, 'owner', 'password', 'owner@brassbandresults.co.uk', 'ABC123', 0, 'A');
