-- USER

CREATE TABLE site_user (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    old_id BIGINT,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    usercode VARCHAR(50) NOT NULL,
      CONSTRAINT cons_site_user_usercode UNIQUE(usercode),
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
    stripe_customer VARCHAR(30),
    new_email_required BIT DEFAULT 0,
    feedback_email_opt_out BIT DEFAULT 0,
    pro_user_for_free BIT DEFAULT 0,
    uuid VARCHAR(40) NOT NULL,
    reset_password_key VARCHAR(40),
    locale VARCHAR(10),
    profile_count_allowed INT DEFAULT 2
);

CREATE UNIQUE INDEX idx_site_user_usercode ON site_user(usercode);
CREATE UNIQUE INDEX idx_site_user_uuid ON site_user(uuid);

INSERT INTO site_user (updated_by, created_by, usercode, password, email, salt, password_version, access_level, uuid) VALUES ('owner', 'owner', 'owner', 'password', 'owner@brassbandresults.co.uk', 'ABC123', 0, 'A', 'ABC123');

-- temporary holding area for an unactivated user
CREATE TABLE site_user_pending (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    usercode VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    salt VARCHAR(10) NOT NULL,
    activation_key VARCHAR(40) NOT NULL
);

CREATE UNIQUE INDEX activation_key_index ON site_user_pending(activation_key);
