-- CONTEST RESULT AWARDS

CREATE TABLE contest_result_award_type (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_result_award_type_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_result_award_type_owner REFERENCES site_user(usercode),
    name VARCHAR(100) NOT NULL
);

CREATE TABLE contest_result_award (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_result_award_updated REFERENCES site_user(usercode),
    created_by VARCHAR(50) NOT NULL CONSTRAINT fk_contest_result_award_owner REFERENCES site_user(usercode),
    contest_result_id BIGINT NOT NULL CONSTRAINT fk_contest_result_award_result REFERENCES contest_result(id),
    award_type_id BIGINT NOT NULL CONSTRAINT fk_contest_result_award_type REFERENCES contest_result_award_type(id),
    description VARCHAR(100)
);

