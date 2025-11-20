CREATE TABLE hotels (
                        id VARCHAR(50) PRIMARY KEY,
                        name VARCHAR(100),
                        opened_on DATE,
                        stars INT,
                        has_spa BOOLEAN,
                        image_url VARCHAR(255)
);

CREATE TABLE rooms (
                       number INT PRIMARY KEY,
                       type VARCHAR(50),
                       price_per_night DOUBLE,
                       sea_view BOOLEAN,
                       photo_url VARCHAR(255)
);

CREATE TABLE guests (
                        id IDENTITY PRIMARY KEY,
                        full_name VARCHAR(100) NOT NULL,
                        dob DATE NOT NULL,
                        email VARCHAR(100),
                        vip BOOLEAN,
                        avatar_url VARCHAR(200)
);