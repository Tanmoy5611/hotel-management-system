-- Remove dependent rows before removing test users
DELETE FROM activity_log;
DELETE FROM stays;
DELETE FROM rooms;
DELETE FROM guests;
DELETE FROM hotels;
DELETE FROM application_user;