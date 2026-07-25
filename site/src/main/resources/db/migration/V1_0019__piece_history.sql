CREATE TABLE piece_history (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    piece_id BIGINT NOT NULL CONSTRAINT fk_piece_history_piece REFERENCES piece(id),
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_piece_history_user REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_piece_history_owner REFERENCES site_user(usercode),
    name VARCHAR(100) NOT NULL,
    composer_id BIGINT CONSTRAINT fk_piece_history_composer REFERENCES person(id),
    arranger_id BIGINT CONSTRAINT fk_piece_history_arranger REFERENCES person(id),
    notes TEXT,
    piece_year VARCHAR(10),
    category VARCHAR(1) NOT NULL,
    percussion_requirements TEXT,
    duration_minutes INTEGER
);
