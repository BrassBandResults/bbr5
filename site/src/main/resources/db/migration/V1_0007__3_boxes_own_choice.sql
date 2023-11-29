UPDATE contest_type
SET draw_one_title ='h.draw',
    points_total_title = 'h.total',
    points_one_title = 'h.adj-a',
    points_two_title = 'h.adj-b',
    points_three_title = 'h.adj-c'
WHERE slug = '3-separate-adjudicator-boxes-own-choice';


