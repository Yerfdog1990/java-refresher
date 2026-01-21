TRUNCATE TABLE student RESTART IDENTITY CASCADE;
TRUNCATE TABLE authority RESTART IDENTITY CASCADE;
TRUNCATE TABLE acl_entry RESTART IDENTITY CASCADE;
TRUNCATE TABLE acl_object_identity RESTART IDENTITY CASCADE;
TRUNCATE TABLE acl_class RESTART IDENTITY CASCADE;
TRUNCATE TABLE acl_sid RESTART IDENTITY CASCADE;

--------------------------------------------------
-- 1. STUDENTS
--------------------------------------------------
INSERT INTO student (username, email, password, date_created)
VALUES
    ('Alice', 'alice@example.com', 'alice123', NOW()),
    ('Carol', 'carol@example.com', 'carol123', NOW()),
    ('Alex', 'alex@example.com', 'alex123', NOW()),
    ('Bob',  'bob@example.com',  'bob123', NOW());

--------------------------------------------------
-- 2. AUTHORITY
--------------------------------------------------
-- Authorities mapped to the students above
-- Alice (1) -> ROLE_USER
-- Carol (2) -> ROLE_USER
-- Alex (3) -> ROLE_USER
-- Bob (4) -> ROLE_ADMIN
INSERT INTO authority (student_id, authority) VALUES
                                                  (1, 'ROLE_USER'),
                                                  (2, 'ROLE_USER'),
                                                  (3, 'ROLE_USER'),
                                                  (4, 'ROLE_ADMIN');

--------------------------------------------------
-- 3. ACL SECURITY IDENTITIES (USERS and ROLES)
--------------------------------------------------
INSERT INTO acl_sid (id, principal, sid)
VALUES
    (5, false, 'ROLE_ADMIN'),
    (6, false, 'ROLE_USER');

--------------------------------------------------
-- 4. ACL CLASS (PROTECTED ENTITY)
-- Must be the FULLY QUALIFIED CLASS NAME
--------------------------------------------------
INSERT INTO acl_class (id, class)
VALUES
    (1, 'springsecurity.lesson2datastructureofacl.persistence.entity.Student');

--------------------------------------------------
-- 5. ACL OBJECT IDENTITIES
-- object_id_identity = student.id
--------------------------------------------------
INSERT INTO acl_object_identity
(id, object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting)
VALUES
    (1, 1, '1', NULL, 5, true), -- Student 1 (Alice), owned by ROLE_ADMIN
    (2, 1, '2', NULL, 5, true), -- Student 2 (Carol), owned by ROLE_ADMIN
    (3, 1, '3', NULL, 5, true), -- Student 3 (Alex), owned by ROLE_ADMIN
    (4, 1, '4', NULL, 5, true); -- Student 4 (Bob), owned by ROLE_ADMIN

--------------------------------------------------
-- 6. ACL ENTRIES (PERMISSIONS)
-- Student masks:
-- READ  = 1
-- WRITE = 2 (CREATE/UPDATE/DELETE often use WRITE or specific ones)
-- CREATE = 4
-- DELETE = 8
-- ADMIN = 16
--------------------------------------------------
INSERT INTO acl_entry
(id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES

(1, 1, 0, 5, 1, true, false, false), -- READ
(2, 1, 1, 5, 2, true, false, false), -- WRITE
(3, 1, 2, 5, 8, true, false, false), -- DELETE

(4, 2, 0, 5, 1, true, false, false),
(5, 2, 1, 5, 2, true, false, false),
(6, 2, 2, 5, 8, true, false, false),

(7, 3, 0, 5, 1, true, false, false),
(8, 3, 1, 5, 2, true, false, false),
(9, 3, 2, 5, 8, true, false, false),

(10, 4, 0, 5, 1, true, false, false),
(11, 4, 1, 5, 2, true, false, false),
(12, 4, 2, 5, 8, true, false, false),

-- ROLE_USER (SID 6) can READ all
(13, 1, 3, 6, 1, true, false, false),
(14, 2, 3, 6, 1, true, false, false),
(15, 3, 3, 6, 1, true, false, false),
(16, 4, 3, 6, 1, true, false, false),

-- Specific permission for Alex (Student 3) - ROLE_USER cannot WRITE
(17, 3, 4, 6, 2, false, false, false);

--------------------------------------------------
-- 7. RESET SEQUENCES (POSTGRESQL SAFETY)
-- Using direct ALTER SEQUENCE statements which are more reliable
--------------------------------------------------
ALTER SEQUENCE student_id_seq RESTART WITH 100;
ALTER SEQUENCE authority_id_seq RESTART WITH 100;
