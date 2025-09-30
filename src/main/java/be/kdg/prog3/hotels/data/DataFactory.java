package be.kdg.prog3.hotels.data;

import be.kdg.prog3.hotels.domain.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * - Two public static fields: a List for each many-to-many entity.
 * - Here many-to-many is Guest <---> Room.
 * - seed() fills both lists with >= 5 items, with real data, and sets relationships.
 */

// two public static lists
public class DataFactory {
    public static List<Guest> guests = new ArrayList<>();
    public static List<Room> rooms = new ArrayList<>();

    // Static method to fill lists with real data
    public static void seed() {
        // Hotels with real attributes
        var h1 = new Hotel("Hotel Plaza Athénée, Paris", LocalDate.of(1913, 5, 20), 5, true,
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb");
        var h2 = new Hotel("The Langham, London", LocalDate.of(1865, 1, 1), 5, false,
                "https://images.unsplash.com/photo-1501117716987-c8e6ec1240b9");
        var h3 = new Hotel("Radisson Blu Strand, Stockholm", LocalDate.of(1912, 4, 15), 4, false,
                "https://images.unsplash.com/photo-1507679799987-c73779587ccf");
        var h4 = new Hotel("Radisson Blu Astrid Hotel, Antwerp",
                LocalDate.of(1998, 6, 15), 4, true,
                "https://cf.bstatic.com/xdata/images/hotel/max1024x768/263927327.jpg");
        var h5 = new Hotel("Hotel Amigo, Brussels",
                LocalDate.of(1957, 9, 1), 5, true,
                "https://cf.bstatic.com/xdata/images/hotel/max1024x768/270660617.jpg");


        // Rooms (>=5, but here is 8 for richness)
        var r101 = new Room(101, RoomType.SINGLE, 150.0, false,
                "https://images.unsplash.com/photo-1560347876-aeef00ee58a1");
        var r102 = new Room(102, RoomType.DOUBLE, 250.0, true,
                "https://images.unsplash.com/photo-1560448070-d5a4b2c48b1b");
        var r201 = new Room(201, RoomType.SUITE, 500.0, true,
                "https://images.unsplash.com/photo-1496412705862-e0088f16f791");
        var r202 = new Room(202, RoomType.DOUBLE, 220.0, false,
                "https://images.unsplash.com/photo-1471115853179-bb1d604434e0");
        var r301 = new Room(301, RoomType.SINGLE, 180.0, true,
                "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267");
        var r302 = new Room(302, RoomType.SUITE, 550.0, false,
                "https://images.unsplash.com/photo-1532298229144-0ec0c57515c7");
        var r401 = new Room(401, RoomType.DOUBLE, 300.0, true,
                "https://images.unsplash.com/photo-1600585154340-be6161a56a0c");
        var r402 = new Room(402, RoomType.SINGLE, 140.0, false,
                "https://images.unsplash.com/photo-1505691938895-1758d7feb511");
        var r501 = new Room(501, RoomType.DOUBLE, 210.0, true,
                "https://images.unsplash.com/photo-1501117716987-c8e6ec1240b9");
        var r502 = new Room(502, RoomType.SUITE, 480.0, false,
                "https://images.unsplash.com/photo-1496412705862-e0088f16f791");
        var r503 = new Room(503, RoomType.SINGLE, 130.0, true,
                "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267");

        var r601 = new Room(601, RoomType.SINGLE, 160.0, false,
                "https://images.unsplash.com/photo-1560347876-aeef00ee58a1");
        var r602 = new Room(602, RoomType.DOUBLE, 240.0, true,
                "https://images.unsplash.com/photo-1560448070-d5a4b2c48b1b");
        var r603 = new Room(603, RoomType.SUITE, 520.0, true,
                "https://images.unsplash.com/photo-1532298229144-0ec0c57515c7");

        // Attach rooms to hotels (many-to-one)
        h1.addRoom(r101);
        h1.addRoom(r102);
        h1.addRoom(r201);

        h2.addRoom(r202);
        h2.addRoom(r301);
        h2.addRoom(r401);

        h3.addRoom(r302);
        h3.addRoom(r402);

        h4.addRoom(r501);
        h4.addRoom(r502);
        h4.addRoom(r503);

        h5.addRoom(r601);
        h5.addRoom(r602);
        h5.addRoom(r603);


        // Guests (>=5, here is 8 guests data)
        var g1 = new Guest("Emma Wilson", LocalDate.of(1990, 4, 10), "emma.wilson@example.com", true,
                "https://images.unsplash.com/photo-1503023345310-bd7c1de61c7d");
        var g2 = new Guest("Liam Johnson", LocalDate.of(1985, 12, 3), "liam.johnson@example.com", false,
                "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg");
        var g3 = new Guest("Sophia Martinez", LocalDate.of(1992, 9, 18), "sophia.martinez@example.com", true,
                "https://images.pexels.com/photos/415829/pexels-photo-415829.jpeg");
        var g4 = new Guest("Noah Smith", LocalDate.of(1988, 7, 25), "noah.smith@example.com", false,
                "https://images.unsplash.com/photo-1527980965255-d3b416303d12");
        var g5 = new Guest("Olivia Garcia", LocalDate.of(1995, 2, 8), "olivia.garcia@example.com", true,
                "https://images.unsplash.com/photo-1494790108377-be9c29b29330");
        var g6 = new Guest("Ethan Brown", LocalDate.of(1991, 6, 15), "ethan.brown@example.com", false,
                "https://images.pexels.com/photos/614810/pexels-photo-614810.jpeg");
        var g7 = new Guest("Mia Chen", LocalDate.of(1997, 3, 22), "mia.chen@example.com", true,
                "https://images.unsplash.com/photo-1544005313-94ddf0286df2");
        var g8 = new Guest("Alexander Rossi", LocalDate.of(1989, 11, 9), "alex.rossi@example.com", false,
                "https://images.pexels.com/photos/91227/pexels-photo-91227.jpeg");
        var g9 = new Guest("Marrison Harri", LocalDate.of(2001, 6, 12), "harri@example.com", true,
                "https://images.unsplash.com/photo-1503023345310-bd7c1de61c7d");
        var g10 = new Guest("Emma Janssens", LocalDate.of(1995, 4, 10), "emma.janssens@example.com", false,
                "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg");
        var g11 = new Guest("Lucas Peeters", LocalDate.of(1988, 11, 21), "lucas.peeters@example.com", true,
                "https://images.pexels.com/photos/415829/pexels-photo-415829.jpeg");
        var g12 = new Guest("Sophie Claes", LocalDate.of(1992, 7, 15), "sophie.claes@example.com", false,
                "https://images.unsplash.com/photo-1527980965255-d3b416303d12");

        // Many-to-many Guest-Room (bookings)
        g1.addRoom(r102);
        g1.addRoom(r201);

        g2.addRoom(r101);
        g2.addRoom(r202);

        g3.addRoom(r301);
        g3.addRoom(r302);

        g4.addRoom(r401);

        g5.addRoom(r102);
        g5.addRoom(r402);

        g6.addRoom(r201);

        g7.addRoom(r301);
        g7.addRoom(r302);

        g8.addRoom(r101);

        g9.addRoom(r501);
        g9.addRoom(r502);
        g10.addRoom(r503);

        g11.addRoom(r601);
        g11.addRoom(r602);
        g12.addRoom(r603);


        // Fill public static lists rooms and guests
        rooms.addAll(List.of(r101, r102, r201, r202, r301, r302, r401, r402,
                r501, r502, r503, r601, r602, r603));
        guests.addAll(List.of(g1, g2, g3, g4, g5, g6, g7, g8, g9, g10, g11, g12));

    }
}