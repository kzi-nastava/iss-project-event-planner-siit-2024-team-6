-- Создание пользователя-организатора
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
