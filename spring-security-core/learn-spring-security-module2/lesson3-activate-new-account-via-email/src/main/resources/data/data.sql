-- Seed three students (plaintext passwords) and their authorities

-- Students: two USERs and one ADMIN
INSERT INTO student (username, email, password, enabled) VALUES
  ('Alice', 'alice@example.com', 'alice123', true),
  ('Bob',   'bob@example.com',   'bob123', true),
  ('Carol', 'carol@example.com', 'carol123', true),
  ('Victor', 'onasvictor8@gmail.com', 'vicky123', true);

-- Authorities mapped to the students above
INSERT INTO authority (student_id, authority) VALUES
  (1, 'ROLE_USER'),
  (2, 'ROLE_ADMIN'),
  (3, 'ROLE_USER');
