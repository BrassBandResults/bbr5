UPDATE contest_type SET draw_one_title = 'h.set-test-draw', draw_two_title = 'h.own-choice-draw' WHERE name = 'Sacred, March, Set Test and Own Choice Contest';
INSERT INTO contest_type(old_id, updated, created, updated_by, created_by, name, slug, translation_key, draw_one_title, draw_two_title, draw_three_title, points_total_title, points_one_title, points_two_title, points_three_title, points_four_title, points_penalty_title, has_test_piece, has_own_choice, has_entertainments, statistics_show, statistics_limit) VALUES (null, '20250503 19:24:02', '20250503 19:24:02', 'owner', 'owner', 'Entertainments (Two Programmes)', 'entertainments-two-programmes-contest', 'contest-types.entertainments-two-programmes-contest','h.draw-one', 'h.draw-two', null, 'h.total', 'h.ents-1', 'h.ents-2', null, null, 'h.penalty', 0, 1, 1, 0, '0');



