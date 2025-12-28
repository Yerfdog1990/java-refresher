-- Cleanup (drop in dependency-safe order where possible)
-- Cleanup (drop in dependency-safe order where possible)
DROP TABLE IF EXISTS security_question;
DROP TABLE IF EXISTS verification_token;
DROP TABLE IF EXISTS password_reset_token;
DROP TABLE IF EXISTS authority;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS security_question_definition;


-- Create the schema for the Students and Authorities tables

CREATE TABLE student (
    id SERIAL PRIMARY KEY,
                       username VARCHAR(50) NOT NULL,
                       email VARCHAR(50) NOT NULL UNIQUE,
                       date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       password VARCHAR(500) NOT NULL,
                       enabled BOOLEAN NOT NULL DEFAULT FALSE
);


CREATE TABLE authority (
    id SERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    authority VARCHAR(50) NOT NULL,
    CONSTRAINT fk_authorities_students FOREIGN KEY (student_id) REFERENCES student(id)
);

CREATE UNIQUE INDEX ix_authority_student_authority ON authority (student_id, authority);

CREATE TABLE verification_token (
    id SERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    student_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_verification_token_student FOREIGN KEY (student_id) REFERENCES student(id)
);

CREATE TABLE password_reset_token (
    id SERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    student_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_password_reset_token_student FOREIGN KEY (student_id) REFERENCES student(id)
);


CREATE TABLE security_question_definition (
    id BIGSERIAL PRIMARY KEY,
    text VARCHAR(255) NOT NULL
);

CREATE TABLE security_question (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL UNIQUE,
    question_definition_id BIGINT NOT NULL,
    answer VARCHAR(255) NOT NULL,
    CONSTRAINT fk_security_question_student FOREIGN KEY (student_id) REFERENCES student(id),
    CONSTRAINT fk_security_question_definition FOREIGN KEY (question_definition_id) REFERENCES security_question_definition(id)
);