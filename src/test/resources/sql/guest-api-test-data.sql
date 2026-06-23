-- Start each guest API test with only the records needed by that test
DELETE FROM activity_log;
DELETE FROM stays;
DELETE FROM rooms;
DELETE FROM guests;
DELETE FROM hotels;
DELETE FROM application_user;

-- Public client-created guests need the protected admin as their owner
INSERT INTO application_user (id, email, password, role)
VALUES (1, 'admin@hotelapp.com', 'test-password', 'ADMIN');

-- Use a regular guest so the GET response can be checked safely
INSERT INTO guests (id, guest_type, full_name, dob, email, avatar_url, discount_percentage, owner_id)
VALUES (1, 'GUEST', 'API Test Guest', '1995-06-15', 'guest@example.com', '/images/guests/guest.jpg', 0, 1);

-- Move the generated id sequence past the explicit guest id
SELECT setval('guests_id_seq', (SELECT MAX(id) FROM guests));