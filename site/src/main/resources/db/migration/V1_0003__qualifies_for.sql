-- contest that a contest qualifies through to
ALTER TABLE contest ADD qualifies_for BIGINT CONSTRAINT fk_contest_qualifies_for REFERENCES contest(id);

-- Add new contest type
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES (30, '20230531 17:00:35', '20340531 17:00:35', 'owner', 'owner', 'March, Hymn and Deportment Contest (Separate Points)', 'march-hymn-deportment-contest-separate-points', 'contest-types.march-hymn-deportment-contest-separate-points','Draw', null, null, 'Total', 'March', 'Hymn', 'Deportment', null, null, 0, 0, 0, 0, '0');
