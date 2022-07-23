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

INSERT INTO user (updated_by_id, owner_id, usercode, password, email, salt, password_version, access_level) VALUES (1, 1, 'owner', 'password', 'owner@brassbandresults.co.uk', 'ABC123', 0, 'A');

CREATE TABLE region (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL REFERENCES user(id),
    owner_id BIGINT NOT NULL REFERENCES user(id),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(60) NOT NULL,
    container_id BIGINT REFERENCES region(id),
    country_code VARCHAR(20),
    latitude VARCHAR(15),
    longitude VARCHAR(15),
    default_map_zoom INTEGER
);

INSERT INTO region(updated_by_id, owner_id, name, slug, country_code) VALUES (1, 1, 'Unknown', 'unknown', 'none');

CREATE TABLE section (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL REFERENCES user(id),
    owner_id BIGINT NOT NULL REFERENCES user(id),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(60) NOT NULL,
    position INT NOT NULL DEFAULT 0,
    map_short_code VARCHAR(1) NOT NULL
);

INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'Excellence', 'excellence', 0, 'C');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'Elite', 'elite', 1, 'C');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'Championship', 'championship', 5, 'C');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'First', 'first', 10, '1');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'Second', 'second', 20, '2');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'Third', 'third', 30, '3');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'Forth', 'fourth', 40, '4');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'Fifth', 'fifth', 50, '5');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'Youth', 'youth', 60, 'Y');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'A Grade', 'a', 110, 'A');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'B Grade', 'b', 120, 'B');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'C Grade', 'c', 130, 'C');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'D Grade', 'd', 140, 'D');

CREATE TABLE band (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL REFERENCES user(id),
    owner_id BIGINT NOT NULL REFERENCES user(id),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(60) NOT NULL,
    website VARCHAR(100),
    region_id BIGINT NOT NULL REFERENCES region(id),
    longitude VARCHAR(15),
    latitude VARCHAR(15),
    notes TEXT,
    mapper_id BIGINT REFERENCES user(id),
    start_date DATE,
    end_date DATE,
    status BIGINT,
    section_id BIGINT REFERENCES section(id),
    twitter_name VARCHAR(100)
);

CREATE TABLE band_rehearsal_night (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL REFERENCES user(id),
    owner_id BIGINT NOT NULL REFERENCES user(id),
    day_number INT NOT NULL,
    band_id BIGINT REFERENCES band(id)
);

CREATE TABLE band_previous_name (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL REFERENCES user(id),
    owner_id BIGINT NOT NULL REFERENCES user(id),
    old_name VARCHAR(100) NOT NULL,
    start_date DATE,
    end_date DATE,
    hidden BIT NOT NULL DEFAULT 0
);

CREATE TABLE band_relationship_type (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL REFERENCES user(id),
    owner_id BIGINT NOT NULL REFERENCES user(id),
    name VARCHAR(100) NOT NULL,
    reverse_name VARCHAR(100) NOT NULL
);

INSERT INTO band_relationship_type (updated_by_id, owner_id, name, reverse_name) values (1, 1, 'Is Parent Of', 'Has Parent Of');
INSERT INTO band_relationship_type (updated_by_id, owner_id, name, reverse_name) values (1, 1, 'Is Senior Band To', 'Has Senior Band Of');
INSERT INTO band_relationship_type (updated_by_id, owner_id, name, reverse_name) values (1, 1, 'Was Subsumed Into', 'Has Parent Of');
INSERT INTO band_relationship_type (updated_by_id, owner_id, name, reverse_name) values (1, 1, 'Split From', 'Has Parent Of');
INSERT INTO band_relationship_type (updated_by_id, owner_id, name, reverse_name) values (1, 1, 'Is Scratch Band From', 'Has Scratch Band');
INSERT INTO band_relationship_type (updated_by_id, owner_id, name, reverse_name) values (1, 1, 'Reformed As', 'Reformed From');

CREATE TABLE band_relationship (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL REFERENCES user(id),
    owner_id BIGINT NOT NULL REFERENCES user(id),
    left_band_id BIGINT REFERENCES band(id),
    left_band_name VARCHAR(100),
    relationship_id BIGINT REFERENCES band_relationship_type(id),
    right_band_id BIGINT REFERENCES band(id),
    right_band_name VARCHAR(100),
    start_date DATE,
    end_date DATE
);
