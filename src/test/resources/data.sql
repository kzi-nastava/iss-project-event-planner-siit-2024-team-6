SET REFERENTIAL_INTEGRITY FALSE;

DELETE FROM events;
DELETE FROM users;

SET REFERENTIAL_INTEGRITY TRUE;
INSERT INTO users (
    id, email, password, name, lastname, is_active, suspended_since, user_type
) VALUES (
             2, 'organizer@test.com', 'encodedpassword', 'John', 'Doe', true, CURRENT_TIMESTAMP, 'Organizer'
         ),
      (3, 'admin@test.com', 'pass', 'Mikhail', 'Doe', true, CURRENT_TIMESTAMP, 'Admin' );

-- Создание общего типа события
INSERT INTO event_types (
    id, name, description, is_deleted
) VALUES (
             1, 'Conference', 'Professional Conference Events', false
         );

-- Создание двух событий, привязанных к organizer_id = 2 и event_type_id = 1
INSERT INTO events (
    id, name, description, max_participants, participants, is_public, place, date, rating,
    latitude, longitude, event_type_id, is_deleted, organizer_id
) VALUES
      (
          101, 'Spring Boot Meetup', 'Discussing Spring Boot and REST APIs', 50, 10, true, 'Main Hall A',
          CURRENT_TIMESTAMP, 4.5, 45.2671, 19.8335, 1, false, 2
      ),
      (
          102, 'Java Conference', 'Deep dive into Java 21 features', 100, 20, true, 'Auditorium B',
          CURRENT_TIMESTAMP, 4.8, 45.2672, 19.8336, 1, false, 2
      );
INSERT INTO activities (
    id, name, description, location, start_time, end_time, event_id
) VALUES (
             201,
             'Opening Speech',
             'Introduction to the conference',
             'Main Hall A',
             DATEADD('DAY', 1, CURRENT_TIMESTAMP),
             DATEADD('HOUR', 1, DATEADD('DAY', 1, CURRENT_TIMESTAMP)),
          101
         );
INSERT INTO users (
    id, email, password, name, lastname, is_active, suspended_since, user_type
) VALUES (
             999999, 'testuser@dummy.com', 'pass', 'Test', 'User', true, CURRENT_TIMESTAMP, 'User'
         );

-- Добавляем отдельного организатора
INSERT INTO users (
    id, email, password, name, lastname, is_active, suspended_since, user_type
) VALUES (
             999998, 'testorg@dummy.com', 'pass', 'Future', 'Org', true, CURRENT_TIMESTAMP, 'Organizer'
         );

-- Добавляем событие в будущем (для future тестов)
INSERT INTO events (
    id, name, description, max_participants, participants, is_public, place, date, rating,
    latitude, longitude, event_type_id, is_deleted, organizer_id
) VALUES (
             999001, 'Future Public Event', 'A test future event', 200, 10, true, 'Room Z',
             DATEADD('DAY', 7, CURRENT_TIMESTAMP), 5.0, 45.2700, 19.8350, 1, false, 999998
         );

-- Добавляем событие, привязанное к userId=999999 через activity
INSERT INTO events (
    id, name, description, max_participants, participants, is_public, place, date, rating,
    latitude, longitude, event_type_id, is_deleted, organizer_id
) VALUES (
             999002, 'User Event', 'For user test', 50, 5, true, 'Room Y',
             CURRENT_TIMESTAMP, 4.0, 45.2710, 19.8360, 1, false, 999998
         );

-- Активность для userId=999999 (связь с событием 999002)
INSERT INTO activities (
    id, name, description, location, start_time, end_time, event_id
) VALUES (
             999003, 'Test Activity', 'Activity for user', 'Room Y',
             DATEADD('HOUR', 1, CURRENT_TIMESTAMP), DATEADD('HOUR', 2, CURRENT_TIMESTAMP), 999002
         );

INSERT INTO user_attends_events (user_id, event_id)
VALUES (999999, 999002);