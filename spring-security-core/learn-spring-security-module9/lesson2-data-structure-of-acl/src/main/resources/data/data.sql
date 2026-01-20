TRUNCATE TABLE student RESTART IDENTITY CASCADE;
TRUNCATE TABLE possession RESTART IDENTITY CASCADE;
TRUNCATE TABLE acl_entry RESTART IDENTITY CASCADE;
TRUNCATE TABLE acl_object_identity RESTART IDENTITY CASCADE;
TRUNCATE TABLE acl_class RESTART IDENTITY CASCADE;
TRUNCATE TABLE acl_sid RESTART IDENTITY CASCADE;

--------------------------------------------------
-- 1. STUDENTS
--------------------------------------------------
INSERT INTO student (username, email, password)
VALUES
    ('eugen', 'eugen@email.com', 'eugen123'),
    ('eric',  'eric@email.com',  'eric123');

--------------------------------------------------
-- 2. POSSESSIONS
-- owner_id references student(id)
--------------------------------------------------
INSERT INTO possession (name, owner_id)
VALUES
    ('Eugen Laptop', 1),
    ('Shared Book',  1),
    ('Eric Phone',   2);

--------------------------------------------------
-- 3. ACL SECURITY IDENTITIES (USERS)
--------------------------------------------------
INSERT INTO acl_sid (id, principal, sid)
VALUES
    (1, true, '1'),  -- Maps to student with ID 1 (eugen)
    (2, true, '2');  -- Maps to student with ID 2 (eric)

--------------------------------------------------
-- 4. ACL CLASS (PROTECTED ENTITY)
-- Must be the FULLY QUALIFIED CLASS NAME
--------------------------------------------------
INSERT INTO acl_class (id, class)
VALUES
    (1, 'springsecurity.lesson2datastructureofacl.persistence.entity.Possession');

--------------------------------------------------
-- 5. ACL OBJECT IDENTITIES
-- object_id_identity = possession.id
--------------------------------------------------
INSERT INTO acl_object_identity
(id, object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting)
VALUES
    (1, 1, '1', NULL, 1, true), -- Eugen Laptop
    (2, 1, '2', NULL, 1, true), -- Shared Book
    (3, 1, '3', NULL, 2, true); -- Eric Phone

--------------------------------------------------
-- 6. ACL ENTRIES (PERMISSIONS)
-- Permission masks:
-- READ  = 1
-- WRITE = 2
-- ADMIN = 16
--------------------------------------------------
INSERT INTO acl_entry
(id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES
-- Eugen has ADMIN on his possessions
(1, 1, 0, 1, 16, true, false, false),
(2, 2, 0, 1, 16, true, false, false),

-- Eugen has WRITE on his possessions
(7, 1, 3, 1, 2, true, false, false),
(8, 2, 4, 1, 2, true, false, false),

-- Eric can READ shared book
(3, 2, 1, 2, 1,  true, false, false),

-- Eric has ADMIN on his phone
(4, 3, 0, 2, 16, true, false, false),

-- Eugen can READ his own possessions (adding explicit READ)
(5, 1, 1, 1, 1, true, false, false),
(6, 2, 2, 1, 1, true, false, false);

--------------------------------------------------
-- 7. RESET SEQUENCES (POSTGRESQL SAFETY)
-- Using direct ALTER SEQUENCE statements which are more reliable
--------------------------------------------------
ALTER SEQUENCE student_id_seq RESTART WITH 100;
ALTER SEQUENCE possession_id_seq RESTART WITH 100;
