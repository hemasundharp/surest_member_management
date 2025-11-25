-- Seed data: roles
INSERT INTO role (id, name) VALUES
    (gen_random_uuid(), 'ADMIN'),
    (gen_random_uuid(), 'USER');

-- Seed data: users
-- Admin user
INSERT INTO user_account (id, username, password_hash)
VALUES
    (gen_random_uuid(), 'admin', '$2a$10$yTFVncGBZpvlgyXa9/Wbo.qkEmrvwhuO3PDRjaguiu/amPpzNRY86');

-- Regular user
INSERT INTO user_account (id, username, password_hash)
VALUES
    (gen_random_uuid(), 'user', '$2a$10$7ZmTIoc977.hD7gL/1hs1uqI3aUpkAcbGXMJi7TP7OUWyodWGydxm');

-- Seed data: user_roles mapping
-- Map Admin user to ADMIN role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM user_account u
JOIN role r ON r.name = 'ADMIN'
WHERE u.username = 'admin';

-- Map regular user to USER role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM user_account u
JOIN role r ON r.name = 'USER'
WHERE u.username = 'user';