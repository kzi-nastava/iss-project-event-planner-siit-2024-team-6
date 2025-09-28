SET REFERENTIAL_INTEGRITY FALSE;
DELETE FROM reservations;
DELETE FROM events;
DELETE FROM event_types;
DELETE FROM offers;     -- instead of services
DELETE FROM categories;
DELETE FROM users;
SET REFERENTIAL_INTEGRITY TRUE;

-- Insert a provider user (no separate providers table)
INSERT INTO users (id, email, password, name, lastname, is_active, suspended_since, user_type)
VALUES (2000, 'provider@test.com', 'pass', 'Prov', 'Ider', true, CURRENT_TIMESTAMP, 'Provider');

-- Insert a category
INSERT INTO categories (id, name, description, is_deleted)
VALUES (4000, 'Photography', 'Photo services', false);

-- Insert an offer (instead of service, linked to provider_id = user.id)
INSERT INTO offers (id, name, category_id, provider_id, offer_type, is_available, is_deleted)
VALUES (5000, 'Event Photography', 4000, 2000, 'Service', true, false);

-- Insert an event type
INSERT INTO event_types (id, name, description, is_deleted)
VALUES (6000, 'Conference', 'Professional events', false);

-- Insert an organizer user
INSERT INTO users (id, email, password, name, lastname, is_active, suspended_since, user_type)
VALUES (2001, 'organizer@test.com', 'pass', 'Org', 'User', true, CURRENT_TIMESTAMP, 'Organizer');

-- Insert an event
INSERT INTO events (
    id, name, description, max_participants, participants, is_public, place, date, rating,
    latitude, longitude, event_type_id, is_deleted, organizer_id
)
VALUES (
           7000, 'Test Conference', 'Dummy event for reservation testing', 100, 10, true, 'Main Hall',
           CURRENT_TIMESTAMP, 4.5, 45.0, 19.0, 6000, false, 2001
       );

-- Insert a reservation (link to offer instead of service)
INSERT INTO reservations (id, event_id, service_id, start_time, end_time, is_canceled)
VALUES (8000, 7000, 5000, DATEADD('HOUR', 1, CURRENT_TIMESTAMP), DATEADD('HOUR', 2, CURRENT_TIMESTAMP), false);
