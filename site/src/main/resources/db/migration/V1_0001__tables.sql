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

INSERT INTO site_user (updated_by_id, owner_id, usercode, password, email, salt, password_version, access_level) VALUES (1, 1, 'owner', 'password', 'owner@brassbandresults.co.uk', 'ABC123', 0, 'A');

-- REGION

CREATE TABLE region (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    old_id BIGINT,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL REFERENCES site_user(id),
    owner_id BIGINT NOT NULL REFERENCES site_user(id),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(60) NOT NULL,
    container_id BIGINT REFERENCES region(id),
    country_code VARCHAR(20),
    latitude VARCHAR(15),
    longitude VARCHAR(15),
    default_map_zoom INTEGER
);

INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code) VALUES (1, 1, 17, 'Unknown', 'unknown', 'none');
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code) VALUES (1, 1, 26, 'Great Britain', 'great-britain', 'gb');

INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, container_id, latitude, longitude, default_map_zoom) VALUES (1, 1, 1, 'Yorkshire', 'yorkshire', 'england', SELECT DISTINCT id FROM region WHERE slug='great-britain', '53.703211', '-1.511536', 9);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, container_id, latitude, longitude, default_map_zoom) VALUES (1, 1, 4, 'London and Southern Counties', 'london-and-southern-counties', 'england', SELECT DISTINCT id FROM region WHERE slug='great-britain', '51.530106', '-0.125198', 6);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, container_id, latitude, longitude, default_map_zoom) VALUES (1, 1, 5, 'Midlands', 'midlands', 'england', SELECT DISTINCT id FROM region WHERE slug='great-britain', '53.153359', '-1.478577', 7);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, container_id, latitude, longitude, default_map_zoom) VALUES (1, 1, 6, 'North of England', 'north', 'england', SELECT DISTINCT id FROM region WHERE slug='great-britain', '54.711929', '-1.221314', 7);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, container_id, latitude, longitude, default_map_zoom) VALUES (1, 1, 2, 'North West', 'north-west', 'england', SELECT DISTINCT id FROM region WHERE slug='great-britain', '53.722717', '-3.352661', 8);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, container_id, latitude, longitude, default_map_zoom) VALUES (1, 1, 8, 'West of England', 'west-england', 'england', SELECT DISTINCT id FROM region WHERE slug='great-britain', '50.889174', '-3.550415', 7);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, container_id, latitude, longitude, default_map_zoom) VALUES (1, 1, 3, 'Wales', 'wales', 'wales', SELECT DISTINCT id FROM region WHERE slug='great-britain', '52.462704', '-3.231812', 7);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, container_id, latitude, longitude, default_map_zoom) VALUES (1, 1, 7, 'Scotland', 'scotland', 'scotland', SELECT DISTINCT id FROM region WHERE slug='great-britain', '57.028774', '-4.442139', 5);

INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 38, 'Chile', 'chile', 'cl', '-36.456636', '-71.7334', 4);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 44, 'China', 'china', 'cn', '33.94336', '103.081053',4);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 23, 'Germany', 'germany', 'de', '51.11042', '10.257568',5);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 20, 'Austria', 'austria', 'at', '47.672786', '14.434204',6);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 46, 'Bermuda', 'bermuda', 'bm', '32.305126', '-64.756908',12);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 45, 'Brazil', 'brazil', 'br', '-10.487812', '-51.518556',4);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 47, 'Ecuador', 'ecuador', 'ec', '-1.186439', '-78.468018',7);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 34, 'Canada', 'canada', 'ca', '51.399206', '-90.410163',4);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 19, 'Italy', 'italy', 'it', '42.633959', '12.960205',5);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 29, 'Faroe Islands', 'faroe-islands', 'fo', '61.845783', '-6.773529',8);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 40, 'Fiji', 'fiji', 'fj', '-17.811456', '178.02063',9);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 30, 'Finland', 'finland', 'fi', '62.165502', '26.403808',5);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 43, 'Namibia', 'namibia', 'na', '-22.43134', '17.043456',6);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 48, 'Jamaica', 'jamaica', 'jm', '18.17195', '-77.360687',9);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 31, 'Japan', 'japan', 'jp', '36.456636', '138.237303',5);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 25, 'Lithuania', 'lithuania', 'lt', '55.37911', '23.616943',7);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 28, 'Luxembourg', 'luxembourg', 'lu', '49.768848', '6.085968',9);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 49, 'Malaysia', 'malaysia', 'my', '3.820408', '109.108886',5);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 11, 'Netherlands', 'netherlands', 'nl', '52.066', '5.106811',7);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 36, 'Philippines', 'philippines', 'ph', '11.480025', '123.347167',6);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 41, 'Portugal', 'portugal', 'pt', '39.639538', '-8.627931',6);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 39, 'Slovakia', 'slovakia', 'sk', '48.770672', '19.498901',7);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 35, 'South Africa', 'south-africa', 'za', '-29.267233', '24.909667',5);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 42, 'Spain', 'spain', 'es', '40.413496', '-3.786622',6);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 50, 'Tonga', 'tonga', 'to', '-21.210019', '-175.177231',11);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 33, 'Uganda', 'uganda', 'ug', '1.406109', '32.384033',7);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 51, 'Singapore', 'singapore', 'sg', '1.360117', '103.80146',11);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 52, 'Taiwan', 'taiwan', 'tw', '23.735069', '120.90271',7);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 53, 'Czech Republic', 'czech-republic', 'cz', '49.815087', '15.266659',7);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 16, 'Australia', 'australia', 'au', '-24.607069', '133.403319',4);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 21 ,'Norway', 'norway', 'no', '65.293468', '13.088378',4);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 27, 'Sweden', 'sweden', 'se', '63.352129', '15.981444',4);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 13, 'Belgium', 'belgium', 'be', '50.857977', '4.338684',8);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 22, 'Denmark', 'denmark', 'dk', '56.492827', '10.0177',6);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 9, 'France', 'france', 'fr', '46.890232', '2.589111',5);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 18, 'New Zealand', 'new-zealand', 'nz', '-41.178654', '174.536131',5);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 12, 'Northern Ireland', 'northern-ireland', 'northernireland', '54.635697', '-6.769409',8);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 24, 'Republic of Ireland', 'republic-ireland', 'ie', '53.225768', '-7.76001',6);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 10, 'Switzerland', 'switzerland', 'ch', '47.070122', '8.28186',7);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 15, 'United States of America', 'united-states-america', 'us', '39.639538', '-97.089851',3);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 55, 'Malawi', 'malawi', 'mw', '-13.539201', '33.988037',6);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 54, 'Kenya', 'kenya', 'ke', '0.703107', '37.811279',6);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 66, 'Angola', 'angola', 'ao', '-12.341299', '17.360007',5);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 59, 'Thailand', 'thailand', 'th', '13.0389361', '101.490104',5);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 60, 'Cyprus', 'cyprus', 'cy', '35.005687', '33.230997',9);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 65, 'India', 'india', 'in', '22.484650', '78.542072',5);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 62, 'Iceland', 'iceland', 'is', '64.886477', '-18.296502',6);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 61, 'Poland', 'poland', 'pl', '52.047466', '18.991556',6);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 63, 'Slovenia', 'slovenia', 'si', '46.22203', '14.88306', 5);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 64, 'Zimbabwe', 'zimbabwe', 'zw', '-19.12662', '29.85349', 5);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 56, 'South Korea', 'south-korea', 'kr', '36.64893', '128.02519', 5);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 57, 'Latvia', 'latvia', 'lv', '56.94111', '24.64261', 5);
INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code, latitude, longitude, default_map_zoom) VALUES (1, 1, 58, 'Estonia', 'estonia', 'ee', '58.72120', '25.75584', 5);

-- SECTION

CREATE TABLE section (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL REFERENCES site_user(id),
    owner_id BIGINT NOT NULL REFERENCES site_user(id),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(60) NOT NULL,
    position INT NOT NULL DEFAULT 0,
    map_short_code VARCHAR(1) NOT NULL
);

INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'Excellence', 'excellence', 10, 'C');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'Elite', 'elite', 20, 'C');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'Championship', 'championship', 30, 'C');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'First', 'first', 110, '1');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'Second', 'second', 120, '2');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'Third', 'third', 130, '3');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'Forth', 'fourth', 140, '4');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'Fifth', 'fifth', 150, '5');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'Youth', 'youth', 160, 'Y');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'A Grade', 'a', 210, 'A');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'B Grade', 'b', 220, 'B');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'C Grade', 'c', 230, 'C');
INSERT INTO section(updated_by_id, owner_id, name, slug, position, map_short_code) VALUES (1, 1, 'D Grade', 'd', 240, 'D');

-- BAND

CREATE TABLE band (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    old_id BIGINT,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL REFERENCES site_user(id),
    owner_id BIGINT NOT NULL REFERENCES site_user(id),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(60) NOT NULL,
    website VARCHAR(100),
    region_id BIGINT NOT NULL REFERENCES region(id),
    longitude VARCHAR(15),
    latitude VARCHAR(15),
    notes TEXT,
    mapper_id BIGINT REFERENCES site_user(id),
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
    updated_by_id BIGINT NOT NULL REFERENCES site_user(id),
    owner_id BIGINT NOT NULL REFERENCES site_user(id),
    day_number INT NOT NULL,
    band_id BIGINT REFERENCES band(id)
);

CREATE TABLE band_previous_name (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL REFERENCES site_user(id),
    owner_id BIGINT NOT NULL REFERENCES site_user(id),
    band_id BIGINT NOT NULL REFERENCES band(id),
    old_name VARCHAR(100) NOT NULL,
    start_date DATE,
    end_date DATE,
    hidden BIT NOT NULL DEFAULT 0
);

CREATE TABLE band_relationship_type (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL REFERENCES site_user(id),
    owner_id BIGINT NOT NULL REFERENCES site_user(id),
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
    updated_by_id BIGINT NOT NULL REFERENCES site_user(id),
    owner_id BIGINT NOT NULL REFERENCES site_user(id),
    left_band_id BIGINT REFERENCES band(id),
    left_band_name VARCHAR(100),
    relationship_id BIGINT REFERENCES band_relationship_type(id),
    right_band_id BIGINT REFERENCES band(id),
    right_band_name VARCHAR(100),
    start_date DATE,
    end_date DATE
);

-- PERSON

CREATE TABLE person (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    old_id BIGINT,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL REFERENCES site_user(id),
    owner_id BIGINT NOT NULL REFERENCES site_user(id),
    first_names VARCHAR(100),
    surname VARCHAR(100) NOT NULL,
    suffix VARCHAR(10),
    slug VARCHAR(60) NOT NULL,
    known_for VARCHAR(100),
    notes TEXT,
    deceased BIT NOT NULL DEFAULT 0,
    start_date DATE,
    end_date DATE
);

CREATE TABLE person_alternative_name (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL REFERENCES site_user(id),
    owner_id BIGINT NOT NULL REFERENCES site_user(id),
    person_id BIGINT NOT NULL REFERENCES person(id),
    name VARCHAR(100) NOT NULL,
    hidden BIT NOT NULL DEFAULT 0
);

CREATE TABLE person_profile (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL REFERENCES site_user(id),
    owner_id BIGINT NOT NULL REFERENCES site_user(id),
    person_id BIGINT NOT NULL REFERENCES person(id),
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
    updated_by_id BIGINT NOT NULL REFERENCES site_user(id),
    owner_id BIGINT NOT NULL REFERENCES site_user(id),
    name VARCHAR(100) NOT NULL,
    reverse_name VARCHAR(100) NOT NULL
);

INSERT INTO person_relationship_type (updated_by_id, owner_id, name, reverse_name) values (1, 1, 'Is Father Of', 'Has Father');
INSERT INTO person_relationship_type (updated_by_id, owner_id, name, reverse_name) values (1, 1, 'Is Mother Of', 'Has Mother');
INSERT INTO person_relationship_type (updated_by_id, owner_id, name, reverse_name) values (1, 1, 'Is Brother Of', 'Has Brother');
INSERT INTO person_relationship_type (updated_by_id, owner_id, name, reverse_name) values (1, 1, 'Is Sister Of', 'Has Sister');
INSERT INTO person_relationship_type (updated_by_id, owner_id, name, reverse_name) values (1, 1, 'Is Married To', 'Is Married To');

CREATE TABLE person_relationship (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL REFERENCES site_user(id),
    owner_id BIGINT NOT NULL REFERENCES site_user(id),
    left_person_id BIGINT REFERENCES band(id),
    left_person_name VARCHAR(100),
    relationship_id BIGINT REFERENCES band_relationship_type(id),
    right_person_id BIGINT REFERENCES band(id),
    right_person_name VARCHAR(100)
);