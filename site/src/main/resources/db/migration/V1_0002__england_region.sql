INSERT INTO region(updated_by_id, owner_id, old_id, name, slug, country_code) VALUES (1, 1, 0, 'England', 'england', 'england');

UPDATE region SET container_id = (SELECT id FROM region WHERE slug = 'england') WHERE slug IN ('yorkshire', 'london-and-southern-counties', 'midlands', 'north', 'north-west', 'west-england');