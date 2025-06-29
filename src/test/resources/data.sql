INSERT INTO users (id, email, password, firstname, lastname, role, is_deleted)
VALUES (2, 'u2@gmail.com', 'encodedpassword', 'Test', 'Organizer', 'ROLE_ORGANIZER', false);

INSERT INTO event_type (id, name, description, is_deleted)
VALUES (1, 'Conference', 'Test conference type', false);

INSERT INTO budget (id, total, available)
VALUES (1, 0, 0);
