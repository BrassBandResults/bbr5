UPDATE contest_type SET points_one_title ='h.test-piece' WHERE points_one_title = 'Test Piece';
UPDATE contest_type SET points_two_title ='h.adj-b' WHERE points_two_title = 'Adj B';

UPDATE contest_type SET has_own_choice = 1 WHERE slug = 'march-hymn-contest';
UPDATE contest_type SET has_own_choice = 1 WHERE slug = 'march-hymn-contest-separate-points';
UPDATE contest_type SET has_own_choice = 1 WHERE slug = 'march-contest-music-and-inspection-points';
UPDATE contest_type SET has_own_choice = 1 WHERE slug = 'march-hymn-deportment-contest-separate-points';
UPDATE contest_type SET has_own_choice = 1 WHERE slug = 'own-choice-from-a-specified-list';

