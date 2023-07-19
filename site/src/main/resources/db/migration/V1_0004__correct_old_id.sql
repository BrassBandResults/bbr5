UPDATE contest_type SET old_id = 31 WHERE old_id = 30 AND name = 'March, Hymn and Deportment Contest (Separate Points)';

DROP INDEX contest_result_test_piece.idx_contest_result_test_piece;
CREATE UNIQUE INDEX idx_contest_result_test_piece ON contest_result_test_piece(contest_result_id, piece_id, suffix);

ALTER TABLE site_feedback ADD old_id BIGINT;
