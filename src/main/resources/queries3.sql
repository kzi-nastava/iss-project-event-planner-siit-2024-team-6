-- Insert Admin user
INSERT INTO users (
    user_type, email, password, photo_url, is_active, suspended_since, name, lastname, address, phone_number
) VALUES
    ('Admin', 'admin@example.com', '$2a$10$hgqQzrtu5USBGDeaG2vzIuHDVd2KYcgy6/jgOyl.U14qBhxj3q6Di',
     'https://novisad.travel/wp-content/uploads/2025/01/Novi-Sad-u-brojkama-2024-vest-scaled.jpg',
     true, NOW(), 'Admin', 'User', 'Admin Street', '123-456-7890');

-- Insert EventType 1
INSERT INTO event_types (name, description, is_deleted)
VALUES ('Conference', 'Professional corporate events for networking and knowledge exchange.', false);

-- Insert EventType 2
INSERT INTO event_types (name, description, is_deleted)
VALUES ('Workshop', 'Interactive workshops for skills development.', false);

-- Свяжем все категории (1, 2, 3) с первым EventType (Conference)
INSERT INTO eventtype_categories (eventtype_id, category_id)
VALUES
    (1, 1),
    (1, 2),
    (1, 3);

-- Свяжем категории 1 и 2 со вторым EventType (Workshop)
INSERT INTO eventtype_categories (eventtype_id, category_id)
VALUES
    (2, 1),
    (2, 2);
