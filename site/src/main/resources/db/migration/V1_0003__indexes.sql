CREATE UNIQUE INDEX idx_siteuser_usercode ON site_user(usercode);

CREATE UNIQUE INDEX idx_region_slug ON region(slug);
CREATE UNIQUE INDEX idx_section_slug ON section(slug);
CREATE UNIQUE INDEX idx_band_slug ON band(slug);
CREATE UNIQUE INDEX idx_person_slug ON person(slug);
CREATE UNIQUE INDEX idx_piece_slug ON piece(slug);

CREATE INDEX idx_band_rehearsal_day_band ON band_rehearsal_day(band_id);
CREATE INDEX idx_band_previous_name_band ON band_previous_name(band_id);
CREATE INDEX idx_person_alternative_name_person ON person_alternative_name(person_id);
CREATE INDEX idx_piece_alternative_name_piece ON piece_alternative_name(piece_id);

CREATE INDEX idx_band_relationship_left ON band_relationship(left_band_id);
CREATE INDEX idx_band_relationship_right ON band_relationship(right_band_id);

CREATE INDEX idx_person_relationship_left ON person_relationship(left_person_id);
CREATE INDEX idx_person_relationahip_right ON person_relationship(right_person_id);

