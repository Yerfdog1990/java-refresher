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
  (3, 'ROLE_USER'),
  (4, 'ROLE_USER');

-- Security questions
insert into security_question_definition (id, text) values (1, 'What is the last name of the teacher who gave you your first failing grade?');
insert into security_question_definition (id, text) values (2, 'What is the first name of the person you first kissed?');
insert into security_question_definition (id, text) values (3, 'What is the name of the place your wedding reception was held?');
insert into security_question_definition (id, text) values (4, 'When you were young, what did you want to be when you grew up?');
insert into security_question_definition (id, text) values (5, 'Where were you New Year''s 2000?');
insert into security_question_definition (id, text) values (6, 'Who was your childhood hero?');
