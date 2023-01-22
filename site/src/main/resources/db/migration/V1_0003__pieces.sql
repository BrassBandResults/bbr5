DROP TABLE contest_test_piece;

-- CONTEST EVENT TEST PIECES
CREATE TABLE contest_event_test_piece (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL CONSTRAINT fk_contest_event_test_piece_updated REFERENCES site_user(id),
    owner_id BIGINT NOT NULL CONSTRAINT fk_contest_event_test_piece_owner REFERENCES site_user(id),
    contest_event_id BIGINT NOT NULL CONSTRAINT fk_contest_event_test_piece_contest_event REFERENCES contest_event(id),
    piece_id BIGINT NOT NULL CONSTRAINT fk_contest_event_test_piece_piece REFERENCES piece(id),
    and_or VARCHAR(1)
);

CREATE UNIQUE INDEX idx_contest_event_test_piece ON contest_event_test_piece(contest_event_id, piece_id);

-- CONTEST RESULT TEST PIECES
CREATE TABLE contest_result_test_piece (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by_id BIGINT NOT NULL CONSTRAINT fk_contest_result_test_piece_updated REFERENCES site_user(id),
    owner_id BIGINT NOT NULL CONSTRAINT fk_contest_result_test_piece_owner REFERENCES site_user(id),
    contest_result_id BIGINT NOT NULL CONSTRAINT fk_contest_result_test_piece_contest_event REFERENCES contest_result(id),
    piece_id BIGINT NOT NULL CONSTRAINT fk_contest_result_test_piece_piece REFERENCES piece(id),
    ordering INT NOT NULL
);

CREATE UNIQUE INDEX idx_contest_result_test_piece ON contest_result_test_piece(contest_result_id, piece_id);
