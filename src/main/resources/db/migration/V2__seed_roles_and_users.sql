-- Seed data: roles
INSERT INTO role (id, name) VALUES
    (gen_random_uuid(), 'ADMIN'),
    (gen_random_uuid(), 'USER');

-- Seed data: users
-- Admin user
INSERT INTO user_account (id, username, password_hash, role_id)
SELECT gen_random_uuid(), 'admin', '$2a$10$yTFVncGBZpvlgyXa9/Wbo.qkEmrvwhuO3PDRjaguiu/amPpzNRY86', r.id
FROM role r WHERE r.name = 'ADMIN';

-- Regular user
INSERT INTO user_account (id, username, password_hash, role_id)
SELECT gen_random_uuid(), 'user', '$2a$10$7ZmTIoc977.hD7gL/1hs1uqI3aUpkAcbGXMJi7TP7OUWyodWGydxm', r.id
FROM role r WHERE r.name = 'USER';
