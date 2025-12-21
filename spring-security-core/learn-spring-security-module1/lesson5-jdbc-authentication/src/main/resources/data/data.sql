-- Seed three students (plaintext passwords) and their authorities

-- Students: two USERs and one ADMIN
INSERT INTO student (username, email, password) VALUES
  ('Alice', 'alice@example.com', 'alice123'),
  ('Bob',   'bob@example.com',   'bob123'),
  ('Carol', 'carol@example.com', 'carol123');

-- Authorities mapped to the students above
INSERT INTO authority (student_id, authority) VALUES
  (1, 'ROLE_USER'),
  (2, 'ROLE_ADMIN'),
  (3, 'ROLE_USER');
