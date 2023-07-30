CREATE INDEX idx_band_region ON band(region_id);
CREATE INDEX idx_band_section ON band(section_id);

CREATE INDEX idx_band_rehearsal_day_band ON band_rehearsal_day(band_id);

CREATE INDEX idx_person_combined_name ON person(combined_name);

CREATE INDEX idx_person_profile_person on person_profile(person_id);

CREATE INDEX idx_piece_composer ON piece(composer_id);
CREATE INDEX idx_piece_arranger ON piece(arranger_id);

CREATE INDEX idx_venue_region ON venue(region_id);

CREATE INDEX idx_contest_group ON contest(contest_group_id);
CREATE INDEX idx_contest_region ON contest(region_id);
CREATE INDEX idx_contest_section ON contest(section_id);
CREATE INDEX idx_contest_qualifies_for ON contest(qualifies_for);

CREATE INDEX idx_contest_event_date ON contest_event(date_of_event);
CREATE INDEX idx_contest_event_venue ON contest_event(venue_id);

CREATE INDEX idx_contest_result_conductor_two ON contest_result(conductor_two_id);
CREATE INDEX idx_contest_result_conductor_three ON contest_result(conductor_three_id);
-- fix name of other indexes here if compressing this script

CREATE INDEX idx_contest_event_test_piece_piece ON contest_event_test_piece(piece_id);
CREATE INDEX idx_contest_event_test_piece_event ON contest_event_test_piece(contest_event_id);

CREATE INDEX idx_contest_result_test_piece_piece ON contest_result_test_piece(piece_id);
CREATE INDEX idx_contest_result_test_piece_result ON contest_result_test_piece(contest_result_id);

CREATE INDEX idx_personal_contest_history_result ON personal_contest_history(result_id);


