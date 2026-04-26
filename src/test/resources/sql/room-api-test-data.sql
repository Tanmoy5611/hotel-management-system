-- Clean old test data before inserting fresh API test records
DELETE FROM stays;
DELETE FROM rooms;
DELETE FROM hotels;

-- Insert one hotel used by Room API integration tests
INSERT INTO hotels (id, hotel_id, name, city, country, stars, has_spa)
VALUES (1, 'api-test-hotel', 'API Test Hotel', 'Antwerp', 'Belgium', 5, false);

-- Insert one room linked to the test hotel for GET and PATCH endpoint tests
INSERT INTO rooms (id, number, type, price_per_night, sea_view, hotel_id, description)
VALUES (1, 101, 'DOUBLE', 120.00, true, 1, 'API test room');
