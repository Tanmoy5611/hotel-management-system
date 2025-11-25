INSERT INTO hotels (id, name, opened_on, stars, has_spa, image_url)
VALUES ('plaza-athenee-paris', 'Hotel Plaza Athénée, Paris', '1913-05-20', 5, TRUE, '/images/hotels/plaza_athene.jpg'),
       ('langham-london', 'The Langham, London', '1865-01-01', 5, FALSE, '/images/hotels/langham.jpg'),
       ('radisson-stockholm', 'Radisson Blu Strand, Stockholm', '1912-04-15', 4, FALSE,
        '/images/hotels/radisson_blu_strand.jpg'),
       ('radisson-antwerp', 'Radisson Blu Astrid Hotel, Antwerp', '1998-06-15', 4, TRUE,
        '/images/hotels/radisson_blu_antwerp.jpg'),
       ('amigo-brussels', 'Hotel Amigo, Brussels', '1957-09-01', 5, TRUE, '/images/hotels/amigo.jpg'),
       ('hilton-old-town', 'Hilton Old Town, Antwerp', '1957-09-01', 5, TRUE, '/images/hotels/hilton.jpg'),
       ('c-hotels-slit', 'C-Hotels Silt, Middelkerke', '2024-03-22', 4, TRUE, '/images/hotels/silt.jpg'),
       ('van-der-valk', 'Van der Valk Hotel, Ghent', '2021-04-01', 4, TRUE, '/images/hotels/van_der.jpg'),

       ('pan-pacific', 'Pan Pacific, London', '2021-09-01', 5, TRUE, '/images/hotels/pan_pacific.jpg');

INSERT INTO rooms (number, type, price_per_night, sea_view, photo_url, hotel_id)
VALUES (101, 'SINGLE', 150.0, FALSE, '/images/rooms/plaza_athene_single.jpg', 'plaza-athenee-paris'),
       (102, 'DOUBLE', 250.0, TRUE, '/images/rooms/plaza_athene_double.jpg', 'plaza-athenee-paris'),
       (201, 'SUITE', 500.0, TRUE, '/images/rooms/plaza_athene_suite.jpg', 'plaza-athenee-paris'),

       (202, 'DOUBLE', 220.0, FALSE, '/images/rooms/langham_double.jpg', 'langham-london'),
       (301, 'SINGLE', 180.0, TRUE, '/images/rooms/langham_single.jpg', 'langham-london'),
       (401, 'SUITE', 450.0, TRUE, '/images/rooms/langham_suite.jpg', 'langham-london'),

       (302, 'SUITE', 550.0, FALSE, '/images/rooms/radisson_blu_strand_suite.jpg', 'radisson-stockholm'),
       (402, 'SINGLE', 140.0, FALSE, '/images/rooms/radisson_blu_strand_single.jpg', 'radisson-stockholm'),
       (403, 'DOUBLE', 350.0, FALSE, '/images/rooms/radisson_blu_strand_double.jpg', 'radisson-stockholm'),

       (501, 'DOUBLE', 210.0, TRUE, '/images/rooms/radisson_blu_antwerp_double.jpg', 'radisson-antwerp'),
       (502, 'SUITE', 480.0, FALSE, '/images/rooms/radisson_blu_antwerp_suite.jpg', 'radisson-antwerp'),
       (503, 'SINGLE', 130.0, TRUE, '/images/rooms/radisson_blu_antwerp_single.jpg', 'radisson-antwerp'),

       (601, 'SINGLE', 160.0, FALSE, '/images/rooms/amigo_single.jpg', 'amigo-brussels'),
       (602, 'DOUBLE', 240.0, TRUE, '/images/rooms/amigo_double.jpg', 'amigo-brussels'),
       (603, 'SUITE', 520.0, TRUE, '/images/rooms/amigo_suite.jpg', 'amigo-brussels'),

       (450, 'SUITE', 540.0, FALSE, '/images/rooms/hilton_suite.jpg', 'hilton-old-town'),
       (480, 'DOUBLE', 260.0, FALSE, '/images/rooms/hilton_double.jpg', 'hilton-old-town'),
       (490, 'SINGLE', 140.0, FALSE, '/images/rooms/hilton_single.jpg', 'hilton-old-town'),

       (533, 'SUITE', 590.0, TRUE, '/images/rooms/silt_suite.jpg', 'c-hotels-slit'),
       (563, 'DOUBLE', 270.0, TRUE, '/images/rooms/silt_double.jpg', 'c-hotels-slit'),
       (579, 'SINGLE', 160.0, TRUE, '/images/rooms/silt_single.jpg', 'c-hotels-slit'),

       (703, 'SUITE', 420.0, TRUE, '/images/rooms/van_der_suite.jpg', 'van-der-valk'),
       (705, 'DOUBLE', 200.0, TRUE, '/images/rooms/van_der_double.jpg', 'van-der-valk'),
       (709, 'SINGLE', 110.0, TRUE, '/images/rooms/van_der_single.jpg', 'van-der-valk'),

       (811, 'SUITE', 565.0, FALSE, '/images/rooms/pan_pacific_suite.jpg', 'pan-pacific'),
       (839, 'DOUBLE', 300.0, FALSE, '/images/rooms/pan_pacific_double.jpg', 'pan-pacific'),
       (857, 'SINGLE', 220.0, FALSE, '/images/rooms/pan_pacific_suite.jpg', 'pan-pacific');

INSERT INTO guests (full_name, dob, email, vip, avatar_url)
VALUES ('Billie Wilson', '1990-04-10', 'billie.wilson@example.com', TRUE, '/images/guests/billie_wilson.jpg'),
       ('Liam Johnson', '1985-12-03', 'liam.johnson@example.com', FALSE, '/images/guests/liam_johnson.jpg'),
       ('Sophia Martinez', '1992-09-18', 'sophia.martinez@example.com', TRUE, '/images/guests/sophia_martinez.jpg'),
       ('Ahanyna Saha', '2002-11-28', 'ahanyna.saha@gmail.com', TRUE, '/images/guests/ahanyna_saha.jpg'),
       ('Olivia Garcia', '1995-02-08', 'olivia.garcia@example.com', TRUE, '/images/guests/olivia_garcia.jpg'),
       ('Ethan Brown', '1991-06-15', 'ethan.brown@example.com', FALSE, '/images/guests/ethan_brown.jpg'),
       ('Mia Chen', '1997-03-22', 'mia.chen@example.com', TRUE, '/images/guests/mia_chen.jpg'),
       ('Alexander Rossi', '1989-11-09', 'alex.rossi@example.com', FALSE, '/images/guests/alexander_rossi.jpg'),
       ('Marrison Harri', '2001-06-12', 'harri@example.com', TRUE, '/images/guests/marrison_harri.jpg'),
       ('Emma Janssens', '1995-04-10', 'emma.janssens@example.com', FALSE, '/images/guests/emma_janssens.jpg'),
       ('Lucas Peeters', '1988-11-21', 'lucas.peeters@example.com', TRUE, '/images/guests/lucas_peeters.jpg'),
       ('Kate Claes', '1992-07-15', 'kate.claes@example.com', FALSE, '/images/guests/kate_claes.jpg'),
       ('Noah Smith', '1988-07-25', 'noah.smith@example.com', FALSE, '/images/guests/noah_smith.jpg');

INSERT INTO rooms_guests (room_number, guest_id)
VALUES (102, 1),
       (201, 1),

       (101, 2),
       (202, 2),

       (301, 3),
       (302, 3),

       (401, 4),

       (102, 5),
       (402, 5),

       (201, 6),

       (301, 7),
       (302, 7),

       (101, 8),

       (501, 9),
       (502, 9),

       (503, 10),

       (601, 11),
       (602, 11),

       (603, 12),

       (403, 13);