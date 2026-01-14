-- Seed three students (plaintext passwords) and their authorities

-- Students with bcrypt hashed passwords
INSERT INTO student (username, email, password, enabled) VALUES
  ('Alice', 'alice@example.com', '{bcrypt}$2a$10$YdY5F3Tn6vibVSo55elH1uAgcOoV5bgzDOcO.cTYtXleo3FYdTtIG', true),
  ('Bob', 'bob@example.com', '{bcrypt}$2a$10$hY7RMY/SQSmf1ZBa.wRjlef5vQTe6gqQSQ8.ToUMtBwcua/znCdDy', true),
  ('Carol', 'carol@example.com', '{bcrypt}$2a$10$LYX1usvUviz73mOWMySOVeCerCuIcJSYXBFV2avb8rK7TC.kSSwVm', true),
  ('Victor', 'onasvictor8@gmail.com', '{bcrypt}$2a$10$k/IbHmIml9uLtGPHRZYRbeP.4.g3WKtQTAx1gdYyScg1PFYLS31s2', true);

-- Authorities mapped to the students above
INSERT INTO authority (student_id, authority) VALUES
  (1, 'ROLE_USER'),
  (2, 'ROLE_ADMIN'),
  (3, 'ROLE_USER'),
  (4, 'ROLE_USER');

-- Security questions
insert into security_question_definition (id, text) values (1, 'What is the last name of the teacher who gave you your first failing grade?');
insert into security_question_definition (id, text) values (2, 'What is the first name of the person you first kissed?');
insert into security_question_definition (id, text) values (3, 'What is the name of the place your wedding reception was held?');
insert into security_question_definition (id, text) values (4, 'When you were young, what did you want to be when you grew up?');
insert into security_question_definition (id, text) values (5, 'Where were you New Year''s 2000?');
insert into security_question_definition (id, text) values (6, 'Who was your childhood hero?');
