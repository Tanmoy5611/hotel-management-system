CREATE TABLE hotels (
                        id VARCHAR(50) PRIMARY KEY,     -- business ID (slug-like)
                        name VARCHAR(100) NOT NULL,
                        city VARCHAR(100) NOT NULL,
                        country VARCHAR(100) NOT NULL,
                        opened_on DATE,
                        stars INT,
                        has_spa BOOLEAN,
                        image_url VARCHAR(255),
                        description VARCHAR(4000)
);


CREATE TABLE rooms (
                       id BIGSERIAL PRIMARY KEY,          -- technical ID (used everywhere)
                       number INT NOT NULL,               -- business attribute
                       type VARCHAR(50),
                       price_per_night DOUBLE PRECISION,
                       sea_view BOOLEAN,
                       photo_url VARCHAR(255),
                       description VARCHAR(1000),
                       hotel_id VARCHAR(50) NOT NULL,
                       CONSTRAINT fk_rooms_hotels
                           FOREIGN KEY (hotel_id) REFERENCES hotels(id),
                       CONSTRAINT uq_room_per_hotel
                           UNIQUE (hotel_id, number)      -- same hotel can't have duplicate room numbers
);

CREATE TABLE guests (
                        id BIGSERIAL PRIMARY KEY,  -- PostgreSQL + H2
                        full_name VARCHAR(100) NOT NULL,
                        dob DATE NOT NULL,
                        email VARCHAR(100),
                        vip BOOLEAN,
                        avatar_url VARCHAR(200),
                        guest_type VARCHAR(20),
                        discount_percentage DOUBLE PRECISION DEFAULT 0
);

CREATE TABLE rooms_guests (
                              room_id BIGINT NOT NULL,
                              guest_id BIGINT NOT NULL,

                              CONSTRAINT fk_rooms_guests_room
                                  FOREIGN KEY (room_id) REFERENCES rooms(id),

                              CONSTRAINT fk_rooms_guests_guest
                                  FOREIGN KEY (guest_id) REFERENCES guests(id),

                              CONSTRAINT pk_rooms_guests
                                  PRIMARY KEY (room_id, guest_id)
);