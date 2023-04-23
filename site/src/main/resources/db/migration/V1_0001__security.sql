-- USER

CREATE TABLE site_user (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    old_id BIGINT,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    usercode VARCHAR(50) NOT NULL,
      CONSTRAINT cons_siteuser_usercode UNIQUE(usercode),
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    salt VARCHAR(10) NOT NULL,
    password_version VARCHAR(1) NOT NULL,
    access_level VARCHAR(1) NOT NULL DEFAULT 'M',
    last_login DATETIME,
    points BIGINT NOT NULL DEFAULT 0,
    contest_history_visibility VARCHAR(1) NOT NULL DEFAULT 'P',
    stripe_email VARCHAR(100),
    stripe_token VARCHAR(30),
    stripe_customer VARCHAR(30)
);

CREATE UNIQUE INDEX idx_siteuser_usercode ON site_user(usercode);

INSERT INTO site_user (updated_by, created_by, usercode, password, email, salt, password_version, access_level) VALUES ('owner', 'owner', 'owner', 'password', 'owner@brassbandresults.co.uk', 'ABC123', 0, 'A');