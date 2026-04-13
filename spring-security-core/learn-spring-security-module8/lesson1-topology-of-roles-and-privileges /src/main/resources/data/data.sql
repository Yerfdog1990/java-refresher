
-- Insert privileges
insert into privilege (privilege, id)
values ('READ_PRIVILEGE', 1)
on conflict (id) do nothing;
insert into privilege (privilege, id)
values ('WRITE_PRIVILEGE', 2)
on conflict (id) do nothing;

-- Insert roles
insert into role (role, id)
values ('ROLE_ADMIN', 3)
on conflict (id) do nothing;
insert into role (role, id)
values ('ROLE_USER', 4)
on conflict (id) do nothing;

-- Map privileges to roles
insert into role_privileges (role_id, privilege_id)
values (3, 1)
on conflict (role_id, privilege_id) do nothing;
insert into role_privileges (role_id, privilege_id)
values (3, 2)
on conflict (role_id, privilege_id) do nothing;
insert into role_privileges (role_id, privilege_id)
values (4, 1)
on conflict (role_id, privilege_id) do nothing;

-- Insert students
insert into student (id, username, email, password, enabled)
values (1, 'Bob', 'bob@example.com', '{bcrypt}$2a$12$auHKCyuby8GtzkY1qH23YuVaJ6pbOfq7zxquoBeBRTwiavVrYk8kC', true)
on conflict (id) do nothing;
insert into student (id, username, email, password, enabled)
values (2, 'Alex', 'alex@example.com', '{bcrypt}$2a$12$xZB9GuHZlM5Vp50gljajv.07kAq7oF24ilzC5yc6rRHnsBlPxRFxG', true)
on conflict (id) do nothing;
insert into student (id, username, email, password, enabled)
values (3, 'Carol', 'carol@example.com', '{bcrypt}$2a$12$MCvjhDi6fRQIIYbr2Fymz.JfZwTfvRh7KTUpd0zcVmE7.JSwB7Ksy', true)
on conflict (id) do nothing;
insert into student (id, username, email, password, enabled)
values (4, 'Alice', 'alice@example.com', '{bcrypt}$2a$12$SkbsvFBqdKe2CWT.xVAobeKwG3D6qsvKyIvCT9sscCqGj5S1dbCcC', true)
on conflict (id) do nothing;

-- Map roles to students
insert into student_roles (student_id, role_id)
values (1, 3)
on conflict (student_id, role_id) do nothing;
insert into student_roles (student_id, role_id)
values (2, 4)
on conflict (student_id, role_id) do nothing;
insert into student_roles (student_id, role_id)
values (3, 4)
on conflict (student_id, role_id) do nothing;
insert into student_roles (student_id, role_id)
values (4, 4)
on conflict (student_id, role_id) do nothing;

-- Security questions
insert into security_question_definition (id, text)
values (1, 'What is the last name of the teacher who gave you your first failing grade?')
on conflict (id) do nothing;
insert into security_question_definition (id, text)
values (2, 'What is the first name of the person you first kissed?')
on conflict (id) do nothing;
insert into security_question_definition (id, text)
values (3, 'What is the name of the place your wedding reception was held?')
on conflict (id) do nothing;
insert into security_question_definition (id, text)
values (4, 'When you were young, what did you want to be when you grew up?')
on conflict (id) do nothing;
insert into security_question_definition (id, text)
values (5, 'Where were you New Year''s 2000?')
on conflict (id) do nothing;
insert into security_question_definition (id, text)
values (6, 'Who was your childhood hero?')
on conflict (id) do nothing;