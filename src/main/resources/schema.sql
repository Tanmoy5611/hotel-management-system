CREATE TABLE hotels
(
    id        VARCHAR(50) PRIMARY KEY,
    name      VARCHAR(100),
    opened_on DATE,
    stars     INT,
    has_spa   BOOLEAN,
    image_url VARCHAR(255)
);

CREATE TABLE rooms
(
    number    INT PRIMARY KEY,
    type      VARCHAR(50),
    price_per_night DOUBLE,
    sea_view  BOOLEAN,
    photo_url VARCHAR(255),
    -- Many-to-One (Room -> Hotel)
    hotel_id  VARCHAR(50) NOT NULL,
    CONSTRAINT fk_rooms_hotels
        FOREIGN KEY (hotel_id) REFERENCES hotels (id)
);

CREATE TABLE guests
(
    id IDENTITY PRIMARY KEY,
    full_name  VARCHAR(100) NOT NULL,
    dob        DATE         NOT NULL,
    email      VARCHAR(100),
    vip        BOOLEAN,
    avatar_url VARCHAR(200)
);

-- Many-to-Many (Room <-> Guest)
CREATE TABLE rooms_guests
(
    room_number INT    NOT NULL,
    guest_id    BIGINT NOT NULL,
    CONSTRAINT fk_rooms_guests_room
        FOREIGN KEY (room_number) REFERENCES rooms (number),
    CONSTRAINT fk_rooms_guests_guest
        FOREIGN KEY (guest_id) REFERENCES guests (id)
);