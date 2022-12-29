CREATE TABLE venue (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    old_id BIGINT,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL REFERENCES site_user(id),
    owner_id BIGINT NOT NULL REFERENCES site_user(id),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(60) NOT NULL,
    region_id BIGINT REFERENCES region(id),
    longitude VARCHAR(15),
    latitude VARCHAR(15),
    notes TEXT,
    exact BIT NOT NULL DEFAULT 0,
    mapper_id BIGINT REFERENCES site_user(id),
    parent_id BIGINT REFERENCES venue(id)
);

CREATE UNIQUE INDEX idx_venue_slug ON venue(slug);


CREATE TABLE contest_tag (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    old_id BIGINT,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL REFERENCES site_user(id),
    owner_id BIGINT NOT NULL REFERENCES site_user(id),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(60) NOT NULL
);

CREATE UNIQUE INDEX idx_contest_tag_slug ON contest_tag(slug);
CREATE UNIQUE INDEX idx_contest_tag_name ON contest_tag(name);

CREATE TABLE contest_group (
     id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
     old_id BIGINT,
     updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
     created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_by_id BIGINT NOT NULL REFERENCES site_user(id),
     owner_id BIGINT NOT NULL REFERENCES site_user(id),
     name VARCHAR(100) NOT NULL,
     slug VARCHAR(60) NOT NULL,
     group_type VARCHAR(1) NOT NULL,
     notes TEXT
);

CREATE UNIQUE INDEX idx_contest_group_slug ON contest_group(slug);

CREATE TABLE contest_group_tags (
    contest_group_id BIGINT NOT NULL REFERENCES contest_group(id),
    contest_tag_id BIGINT NOT NULL REFERENCES contest_tag(id)
);

CREATE UNIQUE INDEX idx_contest_group_tags ON contest_group_tags(contest_group_id, contest_tag_id);