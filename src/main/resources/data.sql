-- Default teacher account
INSERT INTO teacher (username, password, name, email, created_at, updated_at, is_active) 
VALUES ('admin', 'admin', 'Administrator', 'admin@example.com', NOW(), NOW(), true);

-- Default student accounts
INSERT INTO student (username, password, name, email, created_at, updated_at, is_active) 
VALUES 
    ('test1', 'test1', 'Test Student 1', 'test1@example.com', NOW(), NOW(), true),
    ('test2', 'test2', 'Test Student 2', 'test2@example.com', NOW(), NOW(), true),
    ('test3', 'test3', 'Test Student 3', 'test3@example.com', NOW(), NOW(), true),
    ('test4', 'test4', 'Test Student 4', 'test4@example.com', NOW(), NOW(), true),
    ('test5', 'test5', 'Test Student 5', 'test5@example.com', NOW(), NOW(), true),
    ('test6', 'test6', 'Test Student 6', 'test6@example.com', NOW(), NOW(), true),
    ('test7', 'test7', 'Test Student 7', 'test7@example.com', NOW(), NOW(), true);

-- Sample questions with allowed difficulty levels (중, 중상, 상, 최상)
-- Note: These will be added once classroom and lecture data is created
-- Allowed difficulty values: '중', '중상', '상', '최상'