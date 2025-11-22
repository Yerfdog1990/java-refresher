-- Clear existing data (if any)
DELETE FROM Task;
DELETE FROM Campaign;
DELETE FROM Worker;

-- Insert Campaigns
INSERT INTO Campaign(id, code, name, description) VALUES (1, 'C1', 'Campaign 1', 'Description of Campaign 1');
INSERT INTO Campaign(id, code, name, description) VALUES (2, 'C2', 'Campaign 2', 'About Campaign 2');
INSERT INTO Campaign(id, code, name, description) VALUES (3, 'C3', 'Campaign 3', 'About Campaign 3');

-- Insert Worker
INSERT INTO Worker(id, email, first_name, last_name) VALUES (1, 'john@test.com', 'John', 'Doe');

-- Insert Tasks
INSERT INTO Task(id, uuid, name, due_date, description, campaign_id, status)
SELECT 1, RANDOM_UUID(), 'Task 1', '2025-01-12', 'Task 1 Description', id, 0
FROM Campaign WHERE code = 'C1';

INSERT INTO Task(id, uuid, name, due_date, description, campaign_id, status)
SELECT 2, RANDOM_UUID(), 'Task 2', '2025-02-10', 'Task 2 Description', id, 0
FROM Campaign WHERE code = 'C1';

INSERT INTO Task(id, uuid, name, due_date, description, campaign_id, status)
SELECT 3, RANDOM_UUID(), 'Task 3', '2025-03-16', 'Task 3 Description', id, 0
FROM Campaign WHERE code = 'C1';

INSERT INTO Task(id, uuid, name, due_date, description, campaign_id, status, assignee_id)
SELECT 4, RANDOM_UUID(), 'Task 4', '2025-06-25', 'Task 4 Description',
       (SELECT id FROM Campaign WHERE code = 'C2'), 0,
       (SELECT id FROM Worker WHERE email = 'john@test.com');