-- REGION

CREATE TABLE region (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    old_id BIGINT,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_region_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_region_owner REFERENCES site_user(usercode),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(60) NOT NULL,
    container_id BIGINT CONSTRAINT fk_region_container REFERENCES region(id),
    country_code VARCHAR(20),
    latitude VARCHAR(15),
    longitude VARCHAR(15),
    default_map_zoom INTEGER
);

CREATE UNIQUE INDEX idx_region_slug ON region(slug);
CREATE UNIQUE INDEX idx_region_name ON region(name);

INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code) VALUES ('owner', 'owner', 17, 'Unknown', 'unknown', 'none');
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code) VALUES ('owner', 'owner', 0, 'England', 'england', 'england');
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, container_id) VALUES ('owner', 'owner', 26, 'Great Britain', 'great-britain', 'gb', (SELECT id FROM region WHERE slug = 'great-britain'));

INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, container_id, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 1, 'Yorkshire', 'yorkshire', 'england', (SELECT DISTINCT id FROM region WHERE slug='england'), '53.703211', '-1.511536', 9);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, container_id, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 4, 'London and Southern Counties', 'london-and-southern-counties', 'england', (SELECT DISTINCT id FROM region WHERE slug='england'), '51.530106', '-0.125198', 6);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, container_id, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 5, 'Midlands', 'midlands', 'england', (SELECT DISTINCT id FROM region WHERE slug='england'), '53.153359', '-1.478577', 7);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, container_id, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 6, 'North of England', 'north', 'england', (SELECT DISTINCT id FROM region WHERE slug='england'), '54.711929', '-1.221314', 7);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, container_id, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 2, 'North West', 'north-west', 'england', (SELECT DISTINCT id FROM region WHERE slug='england'), '53.722717', '-3.352661', 8);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, container_id, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 8, 'West of England', 'west-england', 'england', (SELECT DISTINCT id FROM region WHERE slug='england'), '50.889174', '-3.550415', 7);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, container_id, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 3, 'Wales', 'wales', 'wales', (SELECT DISTINCT id FROM region WHERE slug='great-britain'), '52.462704', '-3.231812', 7);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, container_id, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 7, 'Scotland', 'scotland', 'scotland', (SELECT DISTINCT id FROM region WHERE slug='great-britain'), '57.028774', '-4.442139', 5);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, container_id, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 67, 'Isle of Man', 'isle-of-man', 'manx', (SELECT DISTINCT id FROM region WHERE slug='great-britain'), '54.255569', '-4.552934', 10);

INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 38, 'Chile', 'chile', 'cl', '-36.456636', '-71.7334', 4);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 44, 'China', 'china', 'cn', '33.94336', '103.081053',4);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 23, 'Germany', 'germany', 'de', '51.11042', '10.257568',5);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 20, 'Austria', 'austria', 'at', '47.672786', '14.434204',6);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 46, 'Bermuda', 'bermuda', 'bm', '32.305126', '-64.756908',12);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 45, 'Brazil', 'brazil', 'br', '-10.487812', '-51.518556',4);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 47, 'Ecuador', 'ecuador', 'ec', '-1.186439', '-78.468018',7);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 34, 'Canada', 'canada', 'ca', '51.399206', '-90.410163',4);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 19, 'Italy', 'italy', 'it', '42.633959', '12.960205',5);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 29, 'Faroe Islands', 'faroe-islands', 'fo', '61.845783', '-6.773529',8);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 40, 'Fiji', 'fiji', 'fj', '-17.811456', '178.02063',9);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 30, 'Finland', 'finland', 'fi', '62.165502', '26.403808',5);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 43, 'Namibia', 'namibia', 'na', '-22.43134', '17.043456',6);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 48, 'Jamaica', 'jamaica', 'jm', '18.17195', '-77.360687',9);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 31, 'Japan', 'japan', 'jp', '36.456636', '138.237303',5);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 25, 'Lithuania', 'lithuania', 'lt', '55.37911', '23.616943',7);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 28, 'Luxembourg', 'luxembourg', 'lu', '49.768848', '6.085968',9);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 49, 'Malaysia', 'malaysia', 'my', '3.820408', '109.108886',5);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 11, 'Netherlands', 'netherlands', 'nl', '52.066', '5.106811',7);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 36, 'Philippines', 'philippines', 'ph', '11.480025', '123.347167',6);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 41, 'Portugal', 'portugal', 'pt', '39.639538', '-8.627931',6);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 39, 'Slovakia', 'slovakia', 'sk', '48.770672', '19.498901',7);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 35, 'South Africa', 'south-africa', 'za', '-29.267233', '24.909667',5);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 42, 'Spain', 'spain', 'es', '40.413496', '-3.786622',6);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 50, 'Tonga', 'tonga', 'to', '-21.210019', '-175.177231',11);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 33, 'Uganda', 'uganda', 'ug', '1.406109', '32.384033',7);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 51, 'Singapore', 'singapore', 'sg', '1.360117', '103.80146',11);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 52, 'Taiwan', 'taiwan', 'tw', '23.735069', '120.90271',7);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 53, 'Czech Republic', 'czech-republic', 'cz', '49.815087', '15.266659',7);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 16, 'Australia', 'australia', 'au', '-24.607069', '133.403319',4);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 21 ,'Norway', 'norway', 'no', '65.293468', '13.088378',4);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 27, 'Sweden', 'sweden', 'se', '63.352129', '15.981444',4);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 13, 'Belgium', 'belgium', 'be', '50.857977', '4.338684',8);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 22, 'Denmark', 'denmark', 'dk', '56.492827', '10.0177',6);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 9, 'France', 'france', 'fr', '46.890232', '2.589111',5);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 18, 'New Zealand', 'new-zealand', 'nz', '-41.178654', '174.536131',5);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 12, 'Northern Ireland', 'northern-ireland', 'northernireland', '54.635697', '-6.769409',8);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 24, 'Republic of Ireland', 'republic-ireland', 'ie', '53.225768', '-7.76001',6);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 10, 'Switzerland', 'switzerland', 'ch', '47.070122', '8.28186',7);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 15, 'United States of America', 'united-states-america', 'us', '39.639538', '-97.089851',3);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 55, 'Malawi', 'malawi', 'mw', '-13.539201', '33.988037',6);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 54, 'Kenya', 'kenya', 'ke', '0.703107', '37.811279',6);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 66, 'Angola', 'angola', 'ao', '-12.341299', '17.360007',5);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 59, 'Thailand', 'thailand', 'th', '13.0389361', '101.490104',5);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 60, 'Cyprus', 'cyprus', 'cy', '35.005687', '33.230997',9);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 65, 'India', 'india', 'in', '22.484650', '78.542072',5);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 62, 'Iceland', 'iceland', 'is', '64.886477', '-18.296502',6);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 61, 'Poland', 'poland', 'pl', '52.047466', '18.991556',6);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 63, 'Slovenia', 'slovenia', 'si', '46.22203', '14.88306', 5);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 64, 'Zimbabwe', 'zimbabwe', 'zw', '-19.12662', '29.85349', 5);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 56, 'South Korea', 'south-korea', 'kr', '36.64893', '128.02519', 5);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 57, 'Latvia', 'latvia', 'lv', '56.94111', '24.64261', 5);
INSERT INTO region(updated_by, created_by, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES ('owner', 'owner', 58, 'Estonia', 'estonia', 'ee', '58.72120', '25.75584', 5);

-- SECTION

CREATE TABLE section (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_section_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_section_owner REFERENCES site_user(usercode),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(60) NOT NULL,
    position INT NOT NULL DEFAULT 0,
    map_short_code VARCHAR(1) NOT NULL,
    translation_key VARCHAR(30) NOT NULL
);

CREATE UNIQUE INDEX idx_section_slug ON section(slug);
CREATE UNIQUE INDEX idx_section_name ON section(name);

INSERT INTO section(updated_by, created_by, name, slug, position, map_short_code, translation_key) VALUES ('owner', 'owner', 'Excellence', 'excellence', 10, 'C', 'section.excellence');
INSERT INTO section(updated_by, created_by, name, slug, position, map_short_code, translation_key) VALUES ('owner', 'owner', 'Elite', 'elite', 20, 'C', 'section.elite');
INSERT INTO section(updated_by, created_by, name, slug, position, map_short_code, translation_key) VALUES ('owner', 'owner', 'Championship', 'championship', 30, 'C', 'section.championship');
INSERT INTO section(updated_by, created_by, name, slug, position, map_short_code, translation_key) VALUES ('owner', 'owner', 'First', 'first', 110, '1', 'section.first');
INSERT INTO section(updated_by, created_by, name, slug, position, map_short_code, translation_key) VALUES ('owner', 'owner', 'Second', 'second', 120, '2', 'section.second');
INSERT INTO section(updated_by, created_by, name, slug, position, map_short_code, translation_key) VALUES ('owner', 'owner', 'Third', 'third', 130, '3', 'section.third');
INSERT INTO section(updated_by, created_by, name, slug, position, map_short_code, translation_key) VALUES ('owner', 'owner', 'Fourth', 'fourth', 140, '4', 'section.fourth');
INSERT INTO section(updated_by, created_by, name, slug, position, map_short_code, translation_key) VALUES ('owner', 'owner', 'Fifth', 'fifth', 150, '5', 'section.fifth');
INSERT INTO section(updated_by, created_by, name, slug, position, map_short_code, translation_key) VALUES ('owner', 'owner', 'Youth', 'youth', 160, 'Y', 'section.youth');
INSERT INTO section(updated_by, created_by, name, slug, position, map_short_code, translation_key) VALUES ('owner', 'owner', 'A Grade', 'a', 210, 'A', 'section.a-grade');
INSERT INTO section(updated_by, created_by, name, slug, position, map_short_code, translation_key) VALUES ('owner', 'owner', 'B Grade', 'b', 220, 'B', 'section.b-grade');
INSERT INTO section(updated_by, created_by, name, slug, position, map_short_code, translation_key) VALUES ('owner', 'owner', 'C Grade', 'c', 230, 'C', 'section.c-grade');
INSERT INTO section(updated_by, created_by, name, slug, position, map_short_code, translation_key) VALUES ('owner', 'owner', 'D Grade', 'd', 240, 'D', 'section.d-grade');

-- BAND

CREATE TABLE band (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    old_id BIGINT,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_band_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_band_owner REFERENCES site_user(usercode),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(60) NOT NULL,
    website VARCHAR(100),
    region_id BIGINT NOT NULL CONSTRAINT fk_band_region REFERENCES region(id),
    longitude VARCHAR(15),
    latitude VARCHAR(15),
    notes TEXT,
    mapped_by VARCHAR(50) CONSTRAINT fk_band_mapper REFERENCES site_user(usercode),
    start_date DATE,
    end_date DATE,
    status BIGINT,
    section_id BIGINT CONSTRAINT fk_band_section REFERENCES section(id),
    twitter_name VARCHAR(100)
);

CREATE UNIQUE INDEX idx_band_slug ON band(slug);

CREATE TABLE band_rehearsal_day (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_band_rehearsal_day_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_band_rehearsal_day_owner REFERENCES site_user(usercode),
    day_number INT NOT NULL,
    details VARCHAR(30),
    band_id BIGINT CONSTRAINT fk_band_rehearsal_day_band REFERENCES band(id)
);

CREATE UNIQUE INDEX idx_band_rehearsal_day ON band_rehearsal_day(day_number, band_id);

CREATE TABLE band_previous_name (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_band_previous_name_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_band_previous_name_owner REFERENCES site_user(usercode),
    band_id BIGINT NOT NULL CONSTRAINT fk_band_previous_name_band REFERENCES band(id),
    old_name VARCHAR(100) NOT NULL,
    start_date DATE,
    end_date DATE,
    hidden BIT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX idx_previous_band_name_unique ON band_previous_name(band_id, old_name);
CREATE INDEX idx_band_previous_name_band ON band_previous_name(band_id);

CREATE TABLE band_relationship_type (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_band_relationship_type_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_band_relationship_type_owner REFERENCES site_user(usercode),
    name VARCHAR(100) NOT NULL,
    reverse_name VARCHAR(100) NOT NULL
);

INSERT INTO band_relationship_type (updated_by, created_by, name, reverse_name) values ('owner', 'owner', 'Is Parent Of', 'Has Parent Of');
INSERT INTO band_relationship_type (updated_by, created_by, name, reverse_name) values ('owner', 'owner', 'Is Senior Band To', 'Has Senior Band Of');
INSERT INTO band_relationship_type (updated_by, created_by, name, reverse_name) values ('owner', 'owner', 'Was Subsumed Into', 'Has Parent Of');
INSERT INTO band_relationship_type (updated_by, created_by, name, reverse_name) values ('owner', 'owner', 'Split From', 'Has Parent Of');
INSERT INTO band_relationship_type (updated_by, created_by, name, reverse_name) values ('owner', 'owner', 'Is Scratch Band From', 'Has Scratch Band');
INSERT INTO band_relationship_type (updated_by, created_by, name, reverse_name) values ('owner', 'owner', 'Reformed As', 'Reformed From');

CREATE TABLE band_relationship (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_band_relationship_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_band_relationship_owner REFERENCES site_user(usercode),
    left_band_id BIGINT CONSTRAINT fk_band_relationship_left_band REFERENCES band(id),
    left_band_name VARCHAR(100),
    relationship_id BIGINT CONSTRAINT fk_band_relationship_type REFERENCES band_relationship_type(id),
    right_band_id BIGINT CONSTRAINT fk_band_relationship_right_band REFERENCES band(id),
    right_band_name VARCHAR(100),
    start_date DATE,
    end_date DATE
);

CREATE INDEX idx_band_relationship_left ON band_relationship(left_band_id);
CREATE INDEX idx_band_relationship_right ON band_relationship(right_band_id);

-- PERSON

CREATE TABLE person (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    old_id BIGINT,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_person_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_person_owner REFERENCES site_user(usercode),
    first_names VARCHAR(100),
    combined_name VARCHAR(200),
    surname VARCHAR(100) NOT NULL,
    suffix VARCHAR(10),
    slug VARCHAR(60) NOT NULL,
    known_for VARCHAR(100),
    notes TEXT,
    deceased BIT NOT NULL DEFAULT 0,
    start_date DATE,
    end_date DATE
);

CREATE UNIQUE INDEX idx_person_slug ON person(slug);
CREATE INDEX idx_person_surname ON person(surname);

CREATE TABLE person_alias (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_person_alias_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_person_alias_owner REFERENCES site_user(usercode),
    person_id BIGINT NOT NULL CONSTRAINT fk_person_alias_person REFERENCES person(id),
    name VARCHAR(100) NOT NULL,
    hidden BIT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX idx_person_alias_unique ON person_alias(person_id, name);
CREATE INDEX idx_person_alias_person ON person_alias(person_id);


CREATE TABLE person_profile (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_person_profile_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_person_profile_owner REFERENCES site_user(usercode),
    person_id BIGINT NOT NULL CONSTRAINT fk_person_profile_person REFERENCES person(id),
    title VARCHAR(10),
    qualifications VARCHAR(30),
    email VARCHAR(50),
    website VARCHAR(50),
    home_phone VARCHAR(20),
    mobile_phone VARCHAR(20),
    address VARCHAR(200),
    profile TEXT NOT NULL,
    visible BIT NOT NULL DEFAULT 0
);

CREATE TABLE person_relationship_type (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_person_relationship_type_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_person_relationship_type_owner REFERENCES site_user(usercode),
    name VARCHAR(100) NOT NULL,
    reverse_name VARCHAR(100) NOT NULL
);

INSERT INTO person_relationship_type (updated_by, created_by, name, reverse_name) values ('owner', 'owner', 'Is Father Of', 'Is Child Of');
INSERT INTO person_relationship_type (updated_by, created_by, name, reverse_name) values ('owner', 'owner', 'Is Mother Of', 'Is Child Of');
INSERT INTO person_relationship_type (updated_by, created_by, name, reverse_name) values ('owner', 'owner', 'Is Brother Of', 'Is Sibling Of');
INSERT INTO person_relationship_type (updated_by, created_by, name, reverse_name) values ('owner', 'owner', 'Is Sister Of', 'Is Sibling Of');
INSERT INTO person_relationship_type (updated_by, created_by, name, reverse_name) values ('owner', 'owner', 'Is Married To', 'Is Married To');
INSERT INTO person_relationship_type (updated_by, created_by, name, reverse_name) values ('owner', 'owner', 'Is Grandparent Of', 'Is Grandchild Of');

CREATE TABLE person_relationship (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_person_relationship_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_person_relationship_owner REFERENCES site_user(usercode),
    left_person_id BIGINT CONSTRAINT fk_person_relationship_left_person REFERENCES band(id),
    left_person_name VARCHAR(100),
    relationship_id BIGINT CONSTRAINT fk_person_relationship_type REFERENCES band_relationship_type(id),
    right_person_id BIGINT CONSTRAINT fk_person_relationship_right_person REFERENCES band(id),
    right_person_name VARCHAR(100)
);

CREATE INDEX idx_person_relationship_left ON person_relationship(left_person_id);
CREATE INDEX idx_person_relationship_right ON person_relationship(right_person_id);

-- PIECE

CREATE TABLE piece (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    old_id BIGINT,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_piece_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_piece_owner REFERENCES site_user(usercode),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(60) NOT NULL,
    composer_id BIGINT CONSTRAINT fk_piece_composer REFERENCES person(id),
    arranger_id BIGINT CONSTRAINT fk_piece_arranger REFERENCES person(id),
    piece_year VARCHAR(4),
    category VARCHAR(1) NOT NULL DEFAULT 'T',
    notes TEXT
);

CREATE UNIQUE INDEX idx_piece_slug ON piece(slug);
CREATE UNIQUE INDEX idx_piece_name ON piece(name, piece_year);

CREATE TABLE piece_alias (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_piece_alias_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_piece_alias_owner REFERENCES site_user(usercode),
    piece_id BIGINT NOT NULL CONSTRAINT fk_piece_alias_piece REFERENCES piece(id),
    name VARCHAR(100) NOT NULL,
    hidden BIT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX idx_piece_alias_unique ON piece_alias(piece_id, name);
CREATE INDEX idx_piece_alias_piece ON piece_alias(piece_id);

-- VENUE

CREATE TABLE venue (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    old_id BIGINT,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_venue_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_venue_owner REFERENCES site_user(usercode),
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(60) NOT NULL,
    region_id BIGINT CONSTRAINT fk_venue_region REFERENCES region(id),
    longitude VARCHAR(15),
    latitude VARCHAR(15),
    notes TEXT,
    exact BIT NOT NULL DEFAULT 0,
    mapped_by VARCHAR(50) CONSTRAINT fk_venue_mapper REFERENCES site_user(usercode),
    parent_id BIGINT CONSTRAINT fk_venue_parent REFERENCES venue(id)
);

CREATE UNIQUE INDEX idx_venue_slug ON venue(slug);
CREATE UNIQUE INDEX idx_venue_name ON venue(name);

CREATE TABLE venue_alias (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    old_id BIGINT,
    start_date DATE,
    end_date DATE,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_venue_alias_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_venue_alias_owner REFERENCES site_user(usercode),
    name VARCHAR(255) NOT NULL,
    venue_id BIGINT NOT NULL CONSTRAINT fk_venue_alias_venue REFERENCES venue(id)
);

CREATE UNIQUE INDEX idx_venue_alt_name_unique ON venue_alias(venue_id, name);

-- CONTESTS


CREATE TABLE contest_tag (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    old_id BIGINT,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_tag_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_tag_owner REFERENCES site_user(usercode),
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
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_group_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_group_owner REFERENCES site_user(usercode),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(60) NOT NULL,
    group_type VARCHAR(1) NOT NULL,
    notes TEXT
);

CREATE UNIQUE INDEX idx_contest_group_slug ON contest_group(slug);
CREATE UNIQUE INDEX idx_contest_group_name ON contest_group(name);

CREATE TABLE contest_group_tag_link (
    contest_group_id BIGINT NOT NULL CONSTRAINT fk_contest_group_tag_link_group REFERENCES contest_group(id),
    contest_tag_id BIGINT NOT NULL CONSTRAINT fk_contest_group_tag_link_contest REFERENCES contest_tag(id)
);

CREATE UNIQUE INDEX idx_contest_group_tags ON contest_group_tag_link(contest_group_id, contest_tag_id);

CREATE TABLE contest_group_alias (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    old_id BIGINT,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_group_alias_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_group_alias_owner REFERENCES site_user(usercode),
    name VARCHAR(100) NOT NULL,
    contest_group_id BIGINT NOT NULL CONSTRAINT fk_contest_group_alias_group REFERENCES contest_group(id)
);

CREATE UNIQUE INDEX idx_contest_group_alt_name_unique ON contest_group_alias(contest_group_id, name);

CREATE TABLE contest_type (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    old_id BIGINT,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_type_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_type_owner REFERENCES site_user(usercode),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(60) NOT NULL,
    translation_key VARCHAR(100) NOT NULL,
    draw_one_title VARCHAR(20) NOT NULL,
    draw_two_title VARCHAR(20),
    draw_three_title VARCHAR(20),
    points_total_title VARCHAR(20) NOT NULL,
    points_one_title VARCHAR(20),
    points_two_title VARCHAR(20),
    points_three_title VARCHAR(20),
    points_four_title VARCHAR(20),
    points_penalty_title VARCHAR(20),
    has_test_piece BIT NOT NULL DEFAULT 0,
    has_own_choice BIT NOT NULL DEFAULT 0,
    has_entertainments BIT NOT NULL DEFAULT 0,
    statistics_show BIT NOT NULL DEFAULT 0,
    statistics_limit INT NOT NULL DEFAULT 2
);

CREATE UNIQUE INDEX idx_contest_type_slug_unique ON contest_type(slug);
CREATE UNIQUE INDEX idx_contest_type_name_unique ON contest_type(name);

INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES ( 2, '20120113 15:40:52', '20120110 21:31:00', 'owner', 'owner', 'Entertainments Contest', 'entertainments-contest', 'contest-types.entertainments-contest','Draw', null, null, 'Total', 'Music', 'Entertainment', null, null, 'Penalty', 0, 0, 1, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES ( 3, '20120113 21:49:17', '20120110 21:31:09', 'owner', 'owner', 'Hymn & Own Choice Test Piece Contest', 'hymn-own-choice-test-piece-contest', 'contest-types.hymn-own-choice-test-piece-contest','Draw', null, null, 'Points', null, null, null, null, null, 0, 1, 0, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES ( 4, '20150606 10:36:43', '20120110 21:31:17', 'owner', 'owner', 'March Contest', 'march-contest', 'contest-types.march-contest','Draw', null, null, 'Points', null, null, null, null, null, 0, 1, 0, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES ( 5, '20120113 21:50:15', '20120110 21:31:37', 'owner', 'owner', 'March, Hymn and Own Choice Test Piece Contest', 'march-hymn-and-own-choice-test-piece-contest', 'contest-types.march-hymn-and-own-choice-test-piece-contest','Draw', null, null, 'Points', null, null, null, null, null, 0, 1, 0, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES ( 6, '20120113 21:50:46', '20120110 21:31:55', 'owner', 'owner', 'March & Hymn Contest', 'march-hymn-contest', 'contest-types.march-hymn-contest','Draw', null, null, 'Points', null, null, null, null, null, 0, 0, 0, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES ( 7, '20120113 21:51:15', '20120110 21:32:08', 'owner', 'owner', 'March & Own Choice Test Piece Contest', 'march-own-choice-test-piece-contest', 'contest-types.march-own-choice-test-piece-contest','Draw', null, null, 'Points', null, null, null, null, null, 0, 1, 0, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES ( 8, '20130203 11:13:31', '20120110 21:32:19', 'owner', 'owner', 'Own Choice from a Specified List', 'own-choice-from-a-specified-list', 'contest-types.own-choice-from-a-specified-list','Draw', null, null, 'Points', null, null, null, null, null, 1, 0, 0, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES ( 9, '20120113 15:42:29', '20120110 21:32:31', 'owner', 'owner', 'Own Choice Test Piece Contest', 'own-choice-test-piece-contest', 'contest-types.own-choice-test-piece-contest','Draw', null, null, 'Points', null, null, null, null, null, 0, 1, 0, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES (10, '20120113 21:52:13', '20120110 21:32:56', 'owner', 'owner', 'Set Test and Entertainments Contest', 'set-test-and-entertainments-contest', 'contest-types.set-test-and-entertainments-contest','Set Test Draw', 'Entertainments Draw', null, 'Total', 'Set Test', 'Entertainment', null, null, 'Penalty', 1, 0, 1, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES (11, '20120113 21:52:22', '20120110 21:33:35', 'owner', 'owner', 'Set Test and Own Choice', 'set-test-and-own-choice', 'contest-types.set-test-and-own-choice','Set Test Draw', 'Own Choice Draw', null, 'Total', 'Set Test', 'Own Choice', null, null, null, 1, 1, 0, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES (12, '20120113 21:52:28', '20120110 21:33:43', 'owner', 'owner', 'Test Piece Contest', 'test-piece-contest', 'contest-types.test-piece-contest','Draw', null, null, 'Points', null, null, null, null, null, 1, 0, 0, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES (13, '20120113 21:51:47', '20120110 21:41:59', 'owner', 'owner', 'Other', 'other', 'contest-types.other','Draw', null, null, 'Points', null, null, null, null, null, 1, 0, 0, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES (14, '20120113 21:49:54', '20120113 21:49:54', 'owner', 'owner', 'Hymn & Own Choice Test Piece Contest (Separate Points)', 'hymn-own-choice-test-piece-contest-separate-points', 'contest-types.hymn-own-choice-test-piece-contest-separate-points','Draw', null, null, 'Total', 'Hymn', 'Test Piece', null, null, null, 1, 1, 0, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES (15, '20120218 23:00:05', '20120113 21:50:34', 'owner', 'owner', 'March, Hymn and Own Choice Test Piece Contest (Separate Points)', 'march-hymn-and-own-choice-test-piece-contest-separate-points', 'contest-types.march-hymn-and-own-choice-test-piece-contest-separate-points','Draw', null, null, 'Total', 'March ', 'Hymn', 'Test Piece', null, null, 0, 1, 0, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES (16, '20120113 21:51:06', '20120113 21:51:06', 'owner', 'owner', 'March & Hymn Contest (Separate Points)', 'march-hymn-contest-separate-points', 'contest-types.march-hymn-contest-separate-points','Draw', null, null, 'Total', 'March', 'Hymn', null, null, null, 0, 0, 0, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES (17, '20120113 21:51:40', '20120113 21:51:40', 'owner', 'owner', 'March & Own Choice Test Piece Contest (Separate Points)', 'march-own-choice-test-piece-contest-separate-points', 'contest-types.march-own-choice-test-piece-contest-separate-points','Draw', null, null, 'Total', 'March', 'Test Piece', null, null, null, 0, 1, 0, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES (18, '20120118 17:00:58', '20120113 21:54:31', 'owner', 'owner', 'Sacred, March, Set Test and Own Choice Contest', 'sacred-march-set-test-and-own-choice-contest', 'contest-types.sacred-march-set-test-and-own-choice-contest','Draw', null, null, 'Total', 'Test Piece', 'Sacred', 'Own Choice', 'Stage March', null, 1, 1, 0, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES (19, '20120118 17:01:07', '20120118 17:01:07', 'owner', 'owner', 'Sacred, March, Concert and Own Choice Contest', 'sacred-march-concert-and-own-choice-contest', 'contest-types.sacred-march-concert-and-own-choice-contest','Draw', null, null, 'Total', 'Concert', 'Sacred', 'Own Choice', 'Stage March', null, 0, 1, 1, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES (20, '20120120 10:05:13', '20120120 10:05:13', 'owner', 'owner', 'March Contest (Music and Inspection Points)', 'march-contest-music-and-inspection-points', 'contest-types.march-contest-music-and-inspection-points','Draw', null, null, 'Points', 'Music ', 'Inspection', null, null, null, 0, 0, 0, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES (21, '20120218 23:00:35', '20120218 23:00:35', 'owner', 'owner', 'March, Solo and Own Choice Test Piece Contest (Separate Points)', 'march-solo-and-own-choice-test-piece-contest-separate-points', 'contest-types.march-solo-and-own-choice-test-piece-contest-separate-points','Draw', null, null, 'Total Points', 'March', 'Solo', 'Own Choice', null, null, 0, 1, 0, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES (22, '20120404 15:03:36', '20120404 15:03:36', 'owner', 'owner', 'Church Concert, Test Piece & Entertainment', 'church-concert-test-piece-entertainment', 'contest-types.church-concert-test-piece-entertainment','Test Piece Draw', 'Entertainment Draw', null, 'Total', 'Church', 'Set Test', 'Entertainment', null, 'Penalty', 1, 0, 1, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES (23, '20130602 18:14:49', '20130602 18:14:49', 'owner', 'owner', 'Entertainments Contest with Deportment Mark', 'entertainments-contest-with-deportment-mark', 'contest-types.entertainments-contest-with-deportment-mark','Draw', null, null, 'Total', 'Music', 'Entertainment', 'Deportment', null, 'Penalty', 0, 0, 1, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES (24, '20130609 19:24:02', '20130609 19:24:02', 'owner', 'owner', 'Own Choice and Entertainments Contest', 'own-choice-and-entertainments-contest', 'contest-types.own-choice-and-entertainments-contest','Draw', 'Entertainments Draw', null, 'Total', 'Test Piece', 'Entertainment', null, null, 'Penalty', 0, 1, 1, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES (25, '20131122 15:52:52', '20131121 11:37:27', 'owner', 'owner', '3 Separate Adjudicator Boxes (Test Piece)', '3-separate-adjudicator-boxes-test-piece', 'contest-types.3-separate-adjudicator-boxes-test-piece','Draw', null, null, 'Total', 'Adj A', 'Adj B', 'Adj C', null, null, 1, 0, 0, 1, '3');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES (26, '20131122 14:00:33', '20131122 14:00:33', 'owner', 'owner', '2 Separate Adjudicator Boxes (Entertainments)', '2-separate-adjudicator-boxes-entertainments', 'contest-types.2-separate-adjudicator-boxes-entertainments','Draw', null, null, 'Points', 'Adj A', 'Adj B', null, null, null, 0, 0, 1, 1, '2');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES (27, '20131122 15:53:02', '20131122 15:21:03', 'owner', 'owner', 'Entertainments (2 Music, 1 Programme, 1 Entertainment points)', 'entertainments-2-music-1-programme-1-entertainment-points', 'contest-types.entertainments-2-music-1-programme-1-entertainment-points','Draw', null, null, 'Total', 'Quality Adj 1', 'Quality Adj 2', 'Programme', 'Entertainment', 'Penalty', 0, 0, 1, 1, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES (28, '20220601 20:41:20', '20220601 20:41:20', 'owner', 'owner', 'Sacred, Set Test and Own Choice Contest', 'sacred-set-test-and-own-choice-contest', 'contest-types.sacred-set-test-and-own-choice-contest','Draw', null, null, 'Total', 'Test Piece', 'Sacred', 'Own Choice', null, null, 1, 1, 0, 0, '0');
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES (29, '20220703 11:06:26', '20220703 11:06:26', 'owner', 'owner', 'March and Entertainments', 'march-and-entertainments', 'contest-types.march-and-entertainments','Draw', null, null, 'Points', 'March', 'Entertainment', 'Music', null, null, 0, 0, 1, 0, '0');

CREATE TABLE contest (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    old_id BIGINT,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_owner REFERENCES site_user(usercode),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(60) NOT NULL,
    contest_group_id BIGINT CONSTRAINT fk_contest_contest_group REFERENCES contest_group(id),
    default_contest_type_id BIGINT NOT NULL CONSTRAINT fk_contest_default_type REFERENCES contest_type(id),
    region_id BIGINT CONSTRAINT fk_contest_region REFERENCES region(id),
    section_id BIGINT CONSTRAINT fk_contest_section REFERENCES section(id),
    ordering INT,
    description TEXT,
    notes TEXT,
    extinct BIT NOT NULL DEFAULT 0,
    exclude_from_group_results BIT NOT NULL DEFAULT 0,
    all_events_added BIT NOT NULL DEFAULT 0,
    prevent_future_bands BIT NOT NULL DEFAULT 0,
    repeat_period INT
);

CREATE UNIQUE INDEX idx_contest_slug_unique ON contest(slug);
CREATE UNIQUE INDEX idx_contest_name_unique ON contest(name);

CREATE TABLE contest_tag_link (
    contest_id BIGINT NOT NULL CONSTRAINT fk_contest_tag_link_contest REFERENCES contest(id),
    contest_tag_id BIGINT NOT NULL CONSTRAINT fk_contest_tag_link_tag REFERENCES contest_tag(id)
);

CREATE UNIQUE INDEX idx_contest_tags ON contest_tag_link(contest_id, contest_tag_id);

CREATE TABLE contest_alias (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    old_id BIGINT,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_alias_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_alias_owner REFERENCES site_user(usercode),
    name VARCHAR(100) NOT NULL,
    contest_id BIGINT NOT NULL CONSTRAINT fk_contest_alias_contest REFERENCES contest(id)
);

CREATE UNIQUE INDEX idx_contest_alt_name_unique ON contest_alias(contest_id, name);

CREATE TABLE contest_event (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    old_id BIGINT,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_event_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_event_owner REFERENCES site_user(usercode),
    name VARCHAR(100) NOT NULL,
    date_of_event DATE NOT NULL,
    date_resolution VARCHAR(1) NOT NULL,
    contest_id BIGINT NOT NULL CONSTRAINT fk_contest_event_contest REFERENCES contest(id),
    notes TEXT,
    venue_id BIGINT CONSTRAINT fk_contest_event_venue REFERENCES venue(id),
    complete BIT NOT NULL DEFAULT 0,
    no_contest BIT NOT NULL DEFAULT 0,
    contest_type_id BIGINT NOT NULL CONSTRAINT fk_contest_event_contest_type REFERENCES contest_type(id),
    original_owner VARCHAR(50) NOT NULL
);

CREATE INDEX idx_contest_event ON contest_event(contest_id);

CREATE TABLE contest_result (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    old_id BIGINT,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_result_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_result_owner REFERENCES site_user(usercode),
    contest_event_id BIGINT NOT NULL CONSTRAINT fk_contest_result_contest_event REFERENCES contest_event(id),
    band_id BIGINT NOT NULL CONSTRAINT fk_contest_result_band REFERENCES band(id),
    band_name VARCHAR(100) NOT NULL,
    result_position_type VARCHAR(1) NOT NULL,
    result_position INT,
    result_award VARCHAR(1),
    draw INT,
    draw_second INT,
    draw_third INT,
    points_total VARCHAR(10),
    points_first VARCHAR(10),
    points_second VARCHAR(10),
    points_third VARCHAR(10),
    points_fourth VARCHAR(10),
    points_penalty VARCHAR(10),
    conductor_name VARCHAR(100),
    conductor_id BIGINT CONSTRAINT fk_contest_result_conductor_one REFERENCES person(id),
    conductor_two_id BIGINT CONSTRAINT fk_contest_result_conductor_two REFERENCES person(id),
    conductor_three_id BIGINT CONSTRAINT fk_contest_result_conductor_three REFERENCES person(id),
    notes TEXT
);

CREATE INDEX idx_contest_result_event ON contest_result(contest_event_id);
CREATE INDEX idx_conductor ON contest_result(conductor_id);
CREATE INDEX idx_band ON contest_result(band_id);

CREATE TABLE contest_event_adjudicator (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    old_id BIGINT,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_event_adjudicator_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_event_adjudicator_owner REFERENCES site_user(usercode),
    contest_event_id BIGINT NOT NULL CONSTRAINT fk_contest_event_adjudicator_contest_event REFERENCES contest_event(id),
    person_id BIGINT NOT NULL CONSTRAINT fk_contest_event_adjudicator_person REFERENCES person(id),
    adjudicator_name VARCHAR(50) NOT NULL
);

CREATE INDEX idx_adjudicator_event ON contest_event_adjudicator(contest_event_id);
CREATE INDEX idx_adjudicator_person ON contest_event_adjudicator(person_id);

-- CONTEST EVENT TEST PIECES
CREATE TABLE contest_event_test_piece (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_event_test_piece_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_event_test_piece_owner REFERENCES site_user(usercode),
    contest_event_id BIGINT NOT NULL CONSTRAINT fk_contest_event_test_piece_contest_event REFERENCES contest_event(id),
    piece_id BIGINT NOT NULL CONSTRAINT fk_contest_event_test_piece_piece REFERENCES piece(id),
    and_or VARCHAR(1)
);

CREATE UNIQUE INDEX idx_contest_event_test_piece ON contest_event_test_piece(contest_event_id, piece_id);

-- CONTEST RESULT TEST PIECES
CREATE TABLE contest_result_test_piece (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_result_test_piece_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_result_test_piece_owner REFERENCES site_user(usercode),
    contest_result_id BIGINT NOT NULL CONSTRAINT fk_contest_result_test_piece_contest_event REFERENCES contest_result(id),
    piece_id BIGINT NOT NULL CONSTRAINT fk_contest_result_test_piece_piece REFERENCES piece(id),
    ordering INT NOT NULL,
    suffix VARCHAR(50)
);

CREATE UNIQUE INDEX idx_contest_result_test_piece ON contest_result_test_piece(contest_result_id, piece_id);

-- FEEDBACK
CREATE TABLE site_feedback (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    url VARCHAR(255) NOT NULL,
    comment TEXT NOT NULL,
    status VARCHAR(1) NOT NULL DEFAULT 'N',
    browser_string VARCHAR(1024) NOT NULL,
    ip VARCHAR(15) NOT NULL,
    additional_comments TEXT,
    audit_log TEXT,
    reported_by VARCHAR(50) CONSTRAINT fk_site_feedback_reporter REFERENCES site_user(usercode),
    created_by VARCHAR(50) CONSTRAINT fk_site_feedback_owner REFERENCES site_user(usercode)
);

-- USER PERFORMANCE HISTORY
CREATE TABLE personal_contest_history (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_personal_contest_history_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_personal_contest_history_owner REFERENCES site_user(usercode),
    result_id BIGINT NOT NULL CONSTRAINT fk_personal_contest_history_result REFERENCES contest_result(id),
    status VARCHAR(1) NOT NULL DEFAULT 'P',
    instrument INTEGER
);

CREATE INDEX idx_personal_history_owner ON personal_contest_history(created_by);

CREATE TABLE personal_contest_history_date_range (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_personal_contest_history_date_range_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_personal_contest_history_date_range_owner REFERENCES site_user(usercode),
    band_id BIGINT NOT NULL CONSTRAINT fk_personal_contest_history_date_range_band REFERENCES band(id),
    start_date DATE NOT NULL,
    end_date DATE,
    imported BIT not null default 0
);

CREATE INDEX idx_personal_history_date_range_owner ON personal_contest_history_date_range(created_by);

-- CONTEST RESULT AWARDS

CREATE TABLE contest_result_award_type (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_result_award_type_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_result_award_type_owner REFERENCES site_user(usercode),
    name VARCHAR(100) NOT NULL
);

CREATE TABLE contest_result_award (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_result_award_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_result_award_owner REFERENCES site_user(usercode),
    contest_result_id BIGINT NOT NULL CONSTRAINT fk_contest_result_award_result REFERENCES contest_result(id),
    award_type_id BIGINT NOT NULL CONSTRAINT fk_contest_result_award_type REFERENCES contest_result_award_type(id),
    description VARCHAR(100)
);

CREATE INDEX idx_contest_result_award_event ON contest_result_award(contest_result_id);