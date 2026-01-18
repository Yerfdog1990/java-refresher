
-- Insert privileges
insert into privilege (privilege, id) values ('READ_PRIVILEGE', 1);
insert into privilege (privilege, id) values ('WRITE_PRIVILEGE', 2);

-- Insert roles
insert into role (role, id) values ('ROLE_ADMIN', 3);
insert into role (role, id) values ('ROLE_USER', 4);

-- Map privileges to roles
insert into role_privileges (role_id, privilege_id) values (3, 1);
insert into role_privileges (role_id, privilege_id) values (3, 2);
insert into role_privileges (role_id, privilege_id) values (4, 1);

-- Insert students
insert into student (id, username, email, password, enabled) values (1, 'Bob','bob@example.com', '{bcrypt}$2a$12$auHKCyuby8GtzkY1qH23YuVaJ6pbOfq7zxquoBeBRTwiavVrYk8kC', true);
insert into student (id, username, email, password, enabled) values (2, 'Alex','alex@example.com', '{bcrypt}$2a$12$xZB9GuHZlM5Vp50gljajv.07kAq7oF24ilzC5yc6rRHnsBlPxRFxG' , true);
insert into student (id, username, email, password, enabled) values (3, 'Alex','carol@example.com', '{bcrypt}$2a$12$MCvjhDi6fRQIIYbr2Fymz.JfZwTfvRh7KTUpd0zcVmE7.JSwB7Ksy' , true);
insert into student (id, username, email, password, enabled) values (4, 'Alex','alice@example.com', '{bcrypt}$2a$12$SkbsvFBqdKe2CWT.xVAobeKwG3D6qsvKyIvCT9sscCqGj5S1dbCcC' , true);

-- Map roles to students
insert into student_roles (student_id, role_id)values (1, 3);
insert into student_roles (student_id, role_id)values (2, 4);
insert into student_roles (student_id, role_id)values (3, 4);
insert into student_roles (student_id, role_id)values (4, 4);

-- Security questions
insert into security_question_definition (id, text) values (1, 'What is the last name of the teacher who gave you your first failing grade?');
insert into security_question_definition (id, text) values (2, 'What is the first name of the person you first kissed?');
insert into security_question_definition (id, text) values (3, 'What is the name of the place your wedding reception was held?');
insert into security_question_definition (id, text) values (4, 'When you were young, what did you want to be when you grew up?');
insert into security_question_definition (id, text) values (5, 'Where were you New Year''s 2000?');
insert into security_question_definition (id, text) values (6, 'Who was your childhood hero?');