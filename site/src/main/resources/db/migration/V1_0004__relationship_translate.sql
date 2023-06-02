UPDATE band_relationship_type SET name='relationship.band.is-parent-of', reverse_name='relationship.band.has-parent-of' WHERE name='Is Parent Of';
UPDATE band_relationship_type SET name='relationship.band.is-senior-band-to', reverse_name='relationship.band.has-senior-band-of' WHERE name='Is Senior Band To';
UPDATE band_relationship_type SET name='relationship.band.was-subsumed-into', reverse_name='relationship.band.has-parent-of' WHERE name='Was Subsumed Into';
UPDATE band_relationship_type SET name='relationship.band.split-from', reverse_name='relationship.band.has-parent-of' WHERE name='Split From';
UPDATE band_relationship_type SET name='relationship.band.is-scratch-band-from', reverse_name='relationship.band.has-scratch-band' WHERE name='Is Scratch Band From';
UPDATE band_relationship_type SET name='relationship.band.reformed-as', reverse_name='relationship.band.reformed-from' WHERE name='Reformed As';

UPDATE person_relationship_type SET name='relationship.person.is-father-of', reverse_name='relationship.person.is-child-of' WHERE name='Is Father Of';
UPDATE person_relationship_type SET name='relationship.person.is-mother-of', reverse_name='relationship.person.is-child-of' WHERE name='Is Mother Of';
UPDATE person_relationship_type SET name='relationship.person.is-brother-of', reverse_name='relationship.person.is-sibling-of' WHERE name='Is Brother Of';
UPDATE person_relationship_type SET name='relationship.person.is-sister-of', reverse_name='relationship.person.is-sibling-of' WHERE name='Is Sister Of';
UPDATE person_relationship_type SET name='relationship.person.is-married-to', reverse_name='relationship.person.is-married-to' WHERE name='Is Married To';
UPDATE person_relationship_type SET name='relationship.person.is-grandparent-of', reverse_name='relationship.person.is-grandchild-of' WHERE name='Is Grandparent Of';