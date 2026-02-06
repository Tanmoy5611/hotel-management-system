-- hotels
INSERT INTO hotels (id, name, city, country, opened_on, stars, has_spa, image_url, description)
VALUES
    ('plaza-athenee-paris', 'Hotel Plaza Athénée, Paris', 'Paris', 'France', '1913-04-20', 5, TRUE, '/images/hotels/plaza_athene.jpg',
     'Inaugurated in 1913, the Hôtel Plaza Athénée is a crown jewel of Parisian luxury located on the prestigious Avenue Montaigne. Famously known for its iconic red awnings and vibrant geraniums, the hotel holds the elite Palace status. It has a deep-rooted history with the fashion world, specifically as the second home to Christian Dior. The property blends French classical and Art Deco styles, featuring the Dior Institut spa and breathtaking views of the Eiffel Tower.'
    ),

    ('langham-london', 'The Langham, London', 'London', 'United Kingdom', '1865-06-10', 5, FALSE, '/images/hotels/langham.jpg',
     'Established in 1865 as Europe’s first "Grand Hotel," The Langham, London has stood as a beacon of luxury for over 150 years. It was a pioneer of modern hospitality, being the first to offer hydraulic lifts and piped hot water. The hotel is famously the birthplace of the traditional Afternoon Tea and has strong literary ties, notably as a setting in Sherlock Holmes stories and a haunt for Oscar Wilde. Located at the prestigious top of Regent Street, it remains a Victorian masterpiece of elegance and history.'
    ),

    ('radisson-stockholm', 'Radisson Blu Strand, Stockholm', 'Stockholm', 'Sweden', '1912-02-24', 4, FALSE,
     '/images/hotels/radisson_blu_strand.jpg',
     'Built for the 1912 Olympic Games, the Radisson Blu Strand is a historic gem overlooking Stockholm’s Nybroviken bay. Designed by Ludwig Peterson, the hotel has a storied past as a cultural hub, famously hosting Hollywood legends like Greta Garbo and Ingrid Bergman. With its striking brick architecture and proximity to the Royal Dramatic Theatre, the Strand offers a unique blend of early 20th-century elegance and contemporary Nordic style. It remains one of Stockholm’s most iconic waterfront properties, deeply connected to the city’s cinematic and sporting history.'
    ),

    ('radisson-antwerp', 'Radisson Blu Astrid Hotel, Antwerp', 'Antwerp', 'Belgium', '1998-06-15', 4, TRUE,
     '/images/hotels/radisson_blu_antwerp.jpg',
     'Located in the heart of the Diamond District, the Radisson Blu Astrid is an architectural landmark designed by Michael Graves. It stands directly across from the breathtaking Antwerpen-Centraal station and just steps from the Antwerp Zoo. Opened in 1998, the hotel is named after Queen Astrid and is famous for its postmodern style and vibrant atmosphere. Featuring a comprehensive health club and spa, it offers guests a luxury experience with unparalleled access to Antwerp’s most famous historic and cultural sites.'
    ),

    ('amigo-brussels', 'Hotel Amigo, Brussels', 'Brussels', 'Belgium', '1957-09-01', 5, TRUE,
     '/images/hotels/amigo.jpg',
     'Located beside the Grand Place, Hotel Amigo is built on the site of a 16th-century prison. Its unique name stems from a Spanish translation error of the old Dutch word for jail. Opened in 1957 for the 1858 World Expo, it has hosted countless celebrities and royals. The hotel famously blends Renaissance history with Belgian pop culture, featuring authentic Flemish decor alongside Tintin-themed elements. As one of the most prestigious addresses in Brussels, it offers a sophisticated sanctuary in the heart of the European capital, complete with a luxury spa and world-class Italian-Belgian cuisine.'
    ),

    ('hilton-old-town', 'Hilton Old Town, Antwerp', 'Antwerp', 'Belgium', '1993-09-01', 5, TRUE,
     '/images/hotels/hilton.jpg',
     'Occupying the historic Grand Bazar building on Antwerp’s Groenplaats, the Hilton Old Town is a landmark of Beaux-Arts elegance. Opened in 1993 after a massive restoration of the Belle Époque facade, the hotel is famous for its grand marble lobby and its location adjacent to the Cathedral of Our Lady. It seamlessly combines the city’s rich commercial history with high-end modern luxury, featuring a signature brasserie and a rooftop terrace. As a cornerstone of Antwerp’s hospitality scene, it offers guests a timeless experience in the cultural and shopping heart of the city.'
    ),

    ('c-hotels-slit', 'C-Hotels Silt, Middelkerke', 'Middelkerke', 'Belgium', '2024-03-22', 4, TRUE,
     '/images/hotels/silt.jpg',
     'Opened in 2024, C-Hotels Silt is a groundbreaking architectural landmark on the Belgian coast. Its unique design, inspired by natural sand dunes, integrates a luxury hotel with a casino and event hall. The sustainable wooden structure is built to harmonize with the North Sea landscape while providing a high-end sanctuary for travelers. With its panoramic sea views and extensive spa facilities, it represents the future of modern coastal hospitality in Middelkerke.'
    ),

    ('van-der-valk', 'Van der Valk Hotel, Ghent', 'Ghent', 'Belgium', '2021-04-01', 4, TRUE,
     '/images/hotels/van_der.jpg',
     'Inaugurated in 2021, Van der Valk Hotel Ghent is a 4-star superior landmark located at the gateway to the city. Situated next to the Ghelamco Arena, the hotel is an architectural focal point known for its modern, sustainable design. Its crown jewel is the 10th-floor Skybar, offering 360-degree views of the Ghent skyline, complemented by a luxury Weleda City Spa and the elegant Restaurant Cocotte. Combining warm hospitality with high-tech amenities like climate-controlled rooms and smart integration, it provides an ideal base for both business and leisure travelers exploring the culture of East Flanders.'
    ),

    ('pan-pacific', 'Pan Pacific, London', 'London', 'United Kingdom', '2021-09-01', 5, TRUE,
     '/images/hotels/pan_pacific.jpg',
     'Inaugurated in 2021 at One Bishopsgate Plaza, Pan Pacific London is a fusion of South East Asian hospitality and British elegance. Designed by Yabu Pushelberg, the hotel is a modern architectural masterpiece in the City of London. It features a unique 1,000-square-meter dedicated well-being floor and an infinity pool with spectacular views of the London skyline. Known for its Straits Kitchen restaurant and high-tech minimalist design, it offers a tranquil, luxury sanctuary for both business travelers and design enthusiasts in the heart of the financial district.'
    );

-- rooms
INSERT INTO rooms (number, type, price_per_night, sea_view, photo_url, description, hotel_id)
VALUES
-- Plaza Athénée, Paris
(101, 'SINGLE', 150.0, FALSE, '/images/rooms/plaza_athene_single.jpg',
 'Cozy single room with elegant décor and modern amenities, ideal for solo travelers seeking comfort and convenience.',
 'plaza-athenee-paris'),

(102, 'DOUBLE', 250.0, TRUE, '/images/rooms/plaza_athene_double.jpg',
 'Spacious double room featuring refined furnishings, premium bedding, and a beautiful sea view—perfect for a relaxing stay for two.',
 'plaza-athenee-paris'),

(201, 'SUITE', 500.0, TRUE, '/images/rooms/plaza_athene_suite.jpg',
 'Luxurious suite offering generous space, sophisticated décor, and stunning sea views, ideal for guests seeking an exceptional stay.',
 'plaza-athenee-paris'),

-- The Langham, London
(202, 'DOUBLE', 220.0, FALSE, '/images/rooms/langham_double.jpg',
 'Elegant double room with classic design, comfortable bedding, and modern facilities, suitable for couples or business travelers.',
 'langham-london'),

(301, 'SINGLE', 180.0, TRUE, '/images/rooms/langham_single.jpg',
 'Bright single room with tasteful décor and a pleasant sea view, offering a comfortable retreat for solo guests.',
 'langham-london'),

(401, 'SUITE', 450.0, TRUE, '/images/rooms/langham_suite.jpg',
 'Spacious suite combining timeless elegance with modern comfort, featuring ample living space and a scenic sea view.',
 'langham-london'),

-- Radisson Blu Strand, Stockholm
(302, 'SUITE', 550.0, FALSE, '/images/rooms/radisson_blu_strand_suite.jpg',
 'Stylish suite with contemporary design and generous living space, perfect for guests who value comfort and privacy.',
 'radisson-stockholm'),

(402, 'SINGLE', 140.0, FALSE, '/images/rooms/radisson_blu_strand_single.jpg',
 'Functional single room with modern amenities, ideal for short stays or business travelers.',
 'radisson-stockholm'),

(403, 'DOUBLE', 350.0, FALSE, '/images/rooms/radisson_blu_strand_double.jpg',
 'Modern double room offering spacious comfort and elegant furnishings, suitable for a relaxed stay in the city.',
 'radisson-stockholm'),

-- Radisson Blu Antwerp
(501, 'DOUBLE', 210.0, TRUE, '/images/rooms/radisson_blu_antwerp_double.jpg',
 'Comfortable double room with contemporary décor and a pleasant sea view, ideal for couples.',
 'radisson-antwerp'),

(502, 'SUITE', 480.0, FALSE, '/images/rooms/radisson_blu_antwerp_suite.jpg',
 'Exclusive suite featuring ample space, refined interiors, and premium amenities for a luxurious stay.',
 'radisson-antwerp'),

(503, 'SINGLE', 130.0, TRUE, '/images/rooms/radisson_blu_antwerp_single.jpg',
 'Compact single room with modern comforts and a refreshing sea view, perfect for solo travelers.',
 'radisson-antwerp'),

-- Hotel Amigo, Brussels
(601, 'SINGLE', 160.0, FALSE, '/images/rooms/amigo_single.jpg',
 'Charming single room with warm décor and essential amenities, offering a quiet and comfortable stay.',
 'amigo-brussels'),

(602, 'DOUBLE', 240.0, TRUE, '/images/rooms/amigo_double.jpg',
 'Stylish double room with elegant furnishings and a scenic sea view, ideal for couples.',
 'amigo-brussels'),

(603, 'SUITE', 520.0, TRUE, '/images/rooms/amigo_suite.jpg',
 'Premium suite with luxurious interiors, spacious layout, and beautiful sea views for an elevated experience.',
 'amigo-brussels'),

-- Hilton Old Town
(450, 'SUITE', 540.0, FALSE, '/images/rooms/hilton_suite.jpg',
 'Modern suite with spacious living areas and upscale amenities, designed for maximum comfort.',
 'hilton-old-town'),

(480, 'DOUBLE', 260.0, FALSE, '/images/rooms/hilton_double.jpg',
 'Well-appointed double room featuring contemporary design and comfortable bedding.',
 'hilton-old-town'),

(490, 'SINGLE', 180.0, FALSE, '/images/rooms/hilton_single.jpg',
 'Simple and comfortable single room, ideal for business or solo travelers.',
 'hilton-old-town'),

-- C-Hotels Silt
(533, 'SUITE', 590.0, TRUE, '/images/rooms/silt_suite.jpg',
 'Exclusive suite offering elegant design, generous space, and stunning sea views.',
 'c-hotels-slit'),

(563, 'DOUBLE', 270.0, TRUE, '/images/rooms/silt_double.jpg',
 'Modern double room with stylish décor and a relaxing sea view, perfect for couples.',
 'c-hotels-slit'),

(579, 'SINGLE', 160.0, TRUE, '/images/rooms/silt_single.jpg',
 'Bright single room with modern furnishings and a pleasant sea view.',
 'c-hotels-slit'),

-- Van der Valk
(703, 'SUITE', 420.0, TRUE, '/images/rooms/van_der_suite.jpg',
 'Comfortable suite with ample space and sea views, ideal for a relaxing getaway.',
 'van-der-valk'),

(705, 'DOUBLE', 200.0, TRUE, '/images/rooms/van_der_double.jpg',
 'Cozy double room featuring modern comforts and a refreshing sea view.',
 'van-der-valk'),

(709, 'SINGLE', 110.0, TRUE, '/images/rooms/van_der_single.jpg',
 'Affordable single room with essential amenities and a sea view, perfect for short stays.',
 'van-der-valk'),

-- Pan Pacific
(811, 'SUITE', 565.0, FALSE, '/images/rooms/pan_pacific_suite.jpg',
 'Elegant suite with contemporary design and spacious living areas for a premium stay.',
 'pan-pacific'),

(839, 'DOUBLE', 300.0, FALSE, '/images/rooms/pan_pacific_double.jpg',
 'Modern double room with refined décor and high-quality amenities.',
 'pan-pacific'),

(857, 'SINGLE', 220.0, FALSE, '/images/rooms/pan_pacific_single.jpg',
 'Comfortable single room with modern facilities, ideal for solo travelers seeking quality.',
 'pan-pacific');

-- guests
INSERT INTO guests (full_name, dob, email, vip, avatar_url, guest_type, discount_percentage)
VALUES ('Billie Wilson', '1990-04-10', 'billie.wilson93@gmail.com', TRUE, '/images/guests/billie_wilson.jpg', 'VIP', 15),
       ('Liam Johnson', '1985-12-03', 'liam.johnson_27@outlook.com', FALSE, '/images/guests/liam_johnson.jpg', 'GUEST', 0),
       ('Sophia Martinez', '1992-09-18', 'sophia.martinez84@yahoo.com', False, '/images/guests/sophia_martinez.jpg', 'GUEST', 0),
       ('Ahanyna Saha', '2002-11-28', 'ahanyna.saha02@gmail.com', TRUE, '/images/guests/ahanyna_saha.jpg', 'VIP', 12),
       ('Olivia Garcia', '1995-02-08', 'olivia.garcia_15@outlook.com', TRUE, '/images/guests/olivia_garcia.jpg', 'VIP', 10),
       ('Ethan Brown', '1991-06-15', 'ethan.brown91@yahoo.com', FALSE, '/images/guests/ethan_brown.jpg', 'GUEST', 0),
       ('Mia Chen', '1997-03-22', 'mia.chen_19@hotmail.com', FALSE, '/images/guests/mia_chen.jpg', 'GUEST', 0),
       ('Alexander Rossi', '1989-11-09', 'alex.rossi89@outlook.com', TRUE, '/images/guests/alexander_rossi.jpg', 'VIP', 8),
       ('Marrison Harri', '2001-06-12', 'marrison.harri01@gmail.com', FALSE, '/images/guests/marrison_harri.jpg', 'GUEST', 0),
       ('Emma Janssens', '1995-04-10', 'emma.janssens95@gmail.com', FALSE, '/images/guests/emma_janssens.jpg', 'GUEST', 0),
       ('Lucas Peeters', '1988-11-21', 'lucas.peeters88@telenet.be', TRUE, '/images/guests/lucas_peeters.jpg', 'VIP', 5),
       ('Kate Claes', '1992-07-15', 'kate.claes92@outlook.com', FALSE, '/images/guests/kate_claes.jpg', 'GUEST', 0),
       ('Noah Smith', '1988-07-25', 'noah.smith_88@yahoo.com', FALSE, '/images/guests/noah_smith.jpg', 'GUEST', 0),
       ('Arthur Evans', '1955-10-10', 'arthur.evans55@gmail.com', FALSE, '/images/guests/arthur_evans.jpg', 'GUEST', 0),
       ('Robert Sterling', '1962-02-20', 'robert.sterling62@outlook.com', TRUE, '/images/guests/robert_sterling.jpg', 'VIP', 20),
       ('Eleanor Vance', '1948-05-18', 'eleanor.vance48@yahoo.com', FALSE, '/images/guests/eleanor_vance.jpg', 'GUEST', 0),
       ('David Chen', '1970-12-05', 'david.chen70@gmail.com', TRUE, '/images/guests/david_chen.jpg', 'VIP', 15),
       ('Gabriel Reyes', '1985-08-25', 'gabriel.reyes85@outlook.com', FALSE, '/images/guests/gabriel_reyes.jpg', 'GUEST', 0),
       ('Anya Lee', '1992-06-01', 'anya.lee92@yahoo.com', FALSE, '/images/guests/anya_lee.jpg', 'GUEST', 0),
       ('Elsa Nordin', '1998-03-15', 'elsa.nordin98@gmail.com', FALSE, '/images/guests/elsa_nordin.jpg', 'GUEST', 0),
       ('Liam Jensen', '2001-07-22', 'liam.jensen01@outlook.com', FALSE, '/images/guests/liam_jensen.jpg', 'GUEST', 0),
       ('Sofia Rossi', '1995-11-28', 'sofia.rossi95@gmail.com', TRUE, '/images/guests/sofia_rossi.jpg', 'VIP', 12),
       ('Ethan Miller', '1997-04-10', 'ethan.miller97@yahoo.com', TRUE, '/images/guests/ethan_miller.jpg', 'VIP', 10),
       ('Ronan OConnell', '2002-09-05', 'ronan.oconnell02@outlook.com', FALSE, '/images/guests/ronan_oconnell.jpg', 'GUEST', 0),
       ('Nancy Sanchez', '2000-01-30', 'nancy.sanchez00@gmail.com', FALSE, '/images/guests/nancy_sanchez.jpg', 'GUEST', 0),
       ('Mark Thompson', '1968-09-12', 'mark.thompson68@outlook.com', TRUE, '/images/guests/mark_thompson.jpg', 'VIP', 5),
       ('Richard Stone', '1961-04-05', 'richard.stone61@yahoo.com', TRUE, '/images/guests/richard_stone.jpg', 'VIP', 14),
       ('Anna Svensson', '1999-06-20', 'anna.svensson99@gmail.com', FALSE, '/images/guests/anna_svensson.jpg', 'GUEST', 0),
       ('Chloe Dubois', '2000-01-15', 'chloe.dubois00@outlook.com', FALSE, '/images/guests/chloe_dubois.jpg', 'GUEST', 0),
       ('Thomas Keller', '1972-03-25', 'thomas.keller72@yahoo.com', TRUE, '/images/guests/thomas_keller.jpg', 'VIP', 8),
       ('Keya Saha', '1996-10-08', 'keya.saha96@gmail.com', TRUE, '/images/guests/keya_saha.jpg', 'VIP', 25);

-- rooms_guests
-- rooms_guests (using room_id instead of room_number)
INSERT INTO rooms_guests (room_id, guest_id)
VALUES
    ((SELECT id FROM rooms WHERE number = 101), 2),
    ((SELECT id FROM rooms WHERE number = 101), 27),
    ((SELECT id FROM rooms WHERE number = 101), 30),
    ((SELECT id FROM rooms WHERE number = 101), 15),
    ((SELECT id FROM rooms WHERE number = 101), 9),
    ((SELECT id FROM rooms WHERE number = 101), 8),

    ((SELECT id FROM rooms WHERE number = 102), 1),
    ((SELECT id FROM rooms WHERE number = 102), 28),
    ((SELECT id FROM rooms WHERE number = 102), 29),
    ((SELECT id FROM rooms WHERE number = 102), 14),
    ((SELECT id FROM rooms WHERE number = 102), 5),

    ((SELECT id FROM rooms WHERE number = 201), 1),
    ((SELECT id FROM rooms WHERE number = 201), 6),
    ((SELECT id FROM rooms WHERE number = 201), 16),
    ((SELECT id FROM rooms WHERE number = 201), 26),
    ((SELECT id FROM rooms WHERE number = 201), 19),
    ((SELECT id FROM rooms WHERE number = 201), 22),

    ((SELECT id FROM rooms WHERE number = 202), 2),
    ((SELECT id FROM rooms WHERE number = 202), 23),
    ((SELECT id FROM rooms WHERE number = 202), 17),

    ((SELECT id FROM rooms WHERE number = 301), 3),
    ((SELECT id FROM rooms WHERE number = 301), 22),
    ((SELECT id FROM rooms WHERE number = 301), 7),

    ((SELECT id FROM rooms WHERE number = 401), 4),
    ((SELECT id FROM rooms WHERE number = 401), 21),
    ((SELECT id FROM rooms WHERE number = 401), 18),
    ((SELECT id FROM rooms WHERE number = 401), 19),
    ((SELECT id FROM rooms WHERE number = 401), 28),
    ((SELECT id FROM rooms WHERE number = 401), 7),
    ((SELECT id FROM rooms WHERE number = 401), 9),

    ((SELECT id FROM rooms WHERE number = 302), 3),
    ((SELECT id FROM rooms WHERE number = 302), 7),

    ((SELECT id FROM rooms WHERE number = 402), 5),

    ((SELECT id FROM rooms WHERE number = 403), 13),
    ((SELECT id FROM rooms WHERE number = 403), 6),
    ((SELECT id FROM rooms WHERE number = 403), 20),
    ((SELECT id FROM rooms WHERE number = 403), 24),

    ((SELECT id FROM rooms WHERE number = 501), 9),
    ((SELECT id FROM rooms WHERE number = 501), 10),

    ((SELECT id FROM rooms WHERE number = 502), 11),
    ((SELECT id FROM rooms WHERE number = 502), 9),

    ((SELECT id FROM rooms WHERE number = 503), 10),
    ((SELECT id FROM rooms WHERE number = 503), 12),
    ((SELECT id FROM rooms WHERE number = 503), 21),

    ((SELECT id FROM rooms WHERE number = 601), 13),
    ((SELECT id FROM rooms WHERE number = 601), 10),
    ((SELECT id FROM rooms WHERE number = 601), 24),
    ((SELECT id FROM rooms WHERE number = 601), 11),

    ((SELECT id FROM rooms WHERE number = 602), 30),
    ((SELECT id FROM rooms WHERE number = 602), 11),

    ((SELECT id FROM rooms WHERE number = 603), 12),
    ((SELECT id FROM rooms WHERE number = 603), 27),

    ((SELECT id FROM rooms WHERE number = 450), 14),
    ((SELECT id FROM rooms WHERE number = 450), 2),

    ((SELECT id FROM rooms WHERE number = 480), 15),

    ((SELECT id FROM rooms WHERE number = 490), 16),
    ((SELECT id FROM rooms WHERE number = 490), 17),
    ((SELECT id FROM rooms WHERE number = 490), 20),

    ((SELECT id FROM rooms WHERE number = 533), 17),

    ((SELECT id FROM rooms WHERE number = 563), 18),
    ((SELECT id FROM rooms WHERE number = 563), 9),
    ((SELECT id FROM rooms WHERE number = 563), 20),

    ((SELECT id FROM rooms WHERE number = 579), 21),
    ((SELECT id FROM rooms WHERE number = 579), 19),
    ((SELECT id FROM rooms WHERE number = 579), 12),
    ((SELECT id FROM rooms WHERE number = 579), 23),

    ((SELECT id FROM rooms WHERE number = 703), 20),

    ((SELECT id FROM rooms WHERE number = 705), 21),

    ((SELECT id FROM rooms WHERE number = 709), 22),
    ((SELECT id FROM rooms WHERE number = 709), 23),
    ((SELECT id FROM rooms WHERE number = 709), 4),

    ((SELECT id FROM rooms WHERE number = 811), 31),
    ((SELECT id FROM rooms WHERE number = 811), 12),

    ((SELECT id FROM rooms WHERE number = 839), 24),
    ((SELECT id FROM rooms WHERE number = 839), 23),

    ((SELECT id FROM rooms WHERE number = 857), 25),
    ((SELECT id FROM rooms WHERE number = 857), 26),
    ((SELECT id FROM rooms WHERE number = 857), 18);