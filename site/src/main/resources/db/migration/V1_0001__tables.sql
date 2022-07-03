CREATE TABLE region (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL,
    owner_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(50) NOT NULL,
    container_id BIGINT REFERENCES region(id),
    country_code VARCHAR(20),
    latitude VARCHAR(15),
    longitude VARCHAR(15),
    default_map_zoom INTEGER
);

CREATE TABLE band (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL,
    owner_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(50) NOT NULL,
    website VARCHAR(100),
    region_id BIGINT NOT NULL REFERENCES region(id),
    rehearsal_night_1 VARCHAR(2),
    rehearsal_night_2 VARCHAR(2),
    longitude VARCHAR(15),
    latitude VARCHAR(15),
    notes TEXT,
    mapper_id BIGINT,
    first_parent_id BIGINT REFERENCES band(id),
    second_parent_id BIGINT REFERENCES band(id),
    start_date DATE,
    end_date DATE,
    status BIGINT,
    national_grading VARCHAR(20),
    twitter_name VARCHAR(100),
    scratch_band BIT NOT NULL DEFAULT 0
);

CREATE TABLE user (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
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