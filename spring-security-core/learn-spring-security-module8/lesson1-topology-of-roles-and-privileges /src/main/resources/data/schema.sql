-- Cleanup (drop dependent tables first)
DROP TABLE IF EXISTS security_question;
DROP TABLE IF EXISTS password_reset_token;
DROP TABLE IF EXISTS verification_token;
DROP TABLE IF EXISTS persistent_logins;

DROP TABLE IF EXISTS student_roles;
DROP TABLE IF EXISTS role_privileges;

DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS privilege;
DROP TABLE IF EXISTS security_question_definition;


-- Create tables in order of dependencies
CREATE TABLE IF NOT EXISTS security_question_definition (
                                                            id SERIAL PRIMARY KEY,
                                                            text VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS role (
                                    id SERIAL PRIMARY KEY,
                                    role VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS privilege (
                                         id SERIAL PRIMARY KEY,
                                         privilege VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS student (
                                       id SERIAL PRIMARY KEY,
                                       username VARCHAR(50) NOT NULL,
                                       email VARCHAR(100) NOT NULL,
                                       date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       password VARCHAR(100) NOT NULL,
                                       enabled BOOLEAN NOT NULL,
                                       security_question_id BIGINT,
                                       security_answer VARCHAR(255),
                                       FOREIGN KEY (security_question_id) REFERENCES security_question_definition(id)
);

CREATE TABLE IF NOT EXISTS role_privileges (
                                                role_id BIGINT NOT NULL,
                                                privilege_id BIGINT NOT NULL,
                                                PRIMARY KEY (role_id, privilege_id),
                                                FOREIGN KEY (role_id) REFERENCES role(id),
                                                FOREIGN KEY (privilege_id) REFERENCES privilege(id)
);

CREATE TABLE IF NOT EXISTS student_roles (
                                             student_id BIGINT NOT NULL,
                                             role_id BIGINT NOT NULL,
                                             PRIMARY KEY (student_id, role_id),
                                             FOREIGN KEY (student_id) REFERENCES student(id),
                                             FOREIGN KEY (role_id) REFERENCES role(id)
);

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

CREATE TABLE security_question (
                                   id BIGSERIAL PRIMARY KEY,
                                   student_id BIGINT NOT NULL UNIQUE,
                                   question_definition_id BIGINT NOT NULL,
                                   answer VARCHAR(255) NOT NULL,
                                   CONSTRAINT fk_security_question_student FOREIGN KEY (student_id) REFERENCES student(id),
                                   CONSTRAINT fk_security_question_definition FOREIGN KEY (question_definition_id) REFERENCES security_question_definition(id)
);


CREATE TABLE persistent_logins (
                                   username VARCHAR(64) NOT NULL,
                                   series VARCHAR(64) PRIMARY KEY,
                                   token VARCHAR(64) NOT NULL,
                                   last_used TIMESTAMP NOT NULL
);