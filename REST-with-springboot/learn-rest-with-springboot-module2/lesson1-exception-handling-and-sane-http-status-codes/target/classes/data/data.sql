
INSERT INTO campaign(code, name, description) VALUES ('C1', 'Campaign 1', 'Description of Campaign 1');
INSERT INTO campaign(code, name, description) VALUES ('C2', 'Campaign 2', 'About Campaign 2');
INSERT INTO campaign(code, name, description) VALUES ('C3', 'Campaign 3', 'About Campaign 3');

INSERT INTO worker(email, first_name, last_name) VALUES ('john@test.com', 'John', 'Doe');

INSERT INTO task(uuid, name, due_date, description, campaign_id, status, assignee_id) VALUES (uuid(), 'Task 1', '2030-01-12', 'Task 1 Description', 1, 0, NULL);
INSERT INTO task(uuid, name, due_date, description, campaign_id, status, assignee_id) VALUES (uuid(), 'Task 2', '2030-02-10', 'Task 2 Description', 1, 0, NULL);
INSERT INTO task(uuid, name, due_date, description, campaign_id, status, assignee_id) VALUES (uuid(), 'Task 3', '2030-03-16', 'Task 3 Description', 1, 0, NULL);
INSERT INTO task(uuid, name, due_date, description, campaign_id, status, assignee_id) VALUES (uuid(), 'Task 4', '2030-06-25', 'Task 4 Description', 2, 0, 1);
