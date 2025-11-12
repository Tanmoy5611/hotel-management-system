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
    public static List<Hotel> hotels = new ArrayList<>();
    public static List<Guest> guests = new ArrayList<>();
    public static List<Room> rooms = new ArrayList<>();

    // Static method to fill lists with real data
    public static void seed() {

        // Clear previous records before seeding fresh sample data
        hotels.clear();
        rooms.clear();
        guests.clear();

        // Hotels with real attributes
        var h1 = new Hotel("plaza-athenee-paris", "Hotel Plaza Athénée, Paris", LocalDate.of(1913, 5, 20), 5, true,
                "/images/hotels/plaza_athene.jpg");
        var h2 = new Hotel("langham-london", "The Langham, London", LocalDate.of(1865, 1, 1), 5, false,
                "/images/hotels/langham.jpg");
        var h3 = new Hotel("radisson-stockholm","Radisson Blu Strand, Stockholm", LocalDate.of(1912, 4, 15), 4, false,
                "/images/hotels/radisson_blu_strand.jpg");
        var h4 = new Hotel("radisson-antwerp", "Radisson Blu Astrid Hotel, Antwerp",
                LocalDate.of(1998, 6, 15), 4, true,
                "/images/hotels/radisson_blu_antwerp.jpg");
        var h5 = new Hotel("amigo-brussels", "Hotel Amigo, Brussels",
                LocalDate.of(1957, 9, 1), 5, true,
                "/images/hotels/amigo.jpg");
        var h6 = new Hotel("hilton-old-town", "Hilton Old Town, Antwerp",
                LocalDate.of(1957, 9, 1), 5, true,
                "/images/hotels/hilton.jpg");
        var h7 = new Hotel("c-hotels-slit", "C-Hotels Silt, Middelkerke",
                LocalDate.of(2024, 3, 22), 4, true,
                "/images/hotels/silt.jpg");
        var h8 = new Hotel("van-der-valk", "Van der Valk Hotel, Ghent",
                LocalDate.of(2021, 4, 1), 4, true,
                "/images/hotels/van_der.jpg");
        var h9 = new Hotel("pan-pacific", "Pan Pacific, London",
                LocalDate.of(2021, 9, 1), 5, true,
                "/images/hotels/pan_pacific.jpg");


        // Rooms (>=5, but here is 9 for richness)
        var r101 = new Room(101, RoomType.SINGLE, 150.0, false,
                "/images/rooms/plaza_athene_single.jpg");
        var r102 = new Room(102, RoomType.DOUBLE, 250.0, true,
                "/images/rooms/plaza_athene_double.jpg");

        var r201 = new Room(201, RoomType.SUITE, 500.0, true,
                "/images/rooms/plaza_athene_suite.jpg");
        var r202 = new Room(202, RoomType.DOUBLE, 220.0, false,
                "/images/rooms/langham_double.jpg");

        var r301 = new Room(301, RoomType.SINGLE, 180.0, true,
                "/images/rooms/langham_single.jpg");
        var r302 = new Room(302, RoomType.SUITE, 550.0, false,
                "/images/rooms/radisson_blu_strand_suite.jpg");

        var r401 = new Room(401, RoomType.SUITE, 450.0, true,
                "/images/rooms/langham_suite.jpg");
        var r402 = new Room(402, RoomType.SINGLE, 140.0, false,
                "/images/rooms/radisson_blu_strand_single.jpg");
        var r403 = new Room(403, RoomType.DOUBLE, 350.0, false,
                "/images/rooms/radisson_blu_strand_double.jpg");

        var r501 = new Room(501, RoomType.DOUBLE, 210.0, true,
                "/images/rooms/radisson_blu_antwerp_double.jpg");
        var r502 = new Room(502, RoomType.SUITE, 480.0, false,
                "/images/rooms/radisson_blu_antwerp_suite.jpg");
        var r503 = new Room(503, RoomType.SINGLE, 130.0, true,
                "/images/rooms/radisson_blu_antwerp_single.jpg");

        var r601 = new Room(601, RoomType.SINGLE, 160.0, false,
                "/images/rooms/amigo_single.jpg");
        var r602 = new Room(602, RoomType.DOUBLE, 240.0, true,
                "/images/rooms/amigo_double.jpg");
        var r603 = new Room(603, RoomType.SUITE, 520.0, true,
                "/images/rooms/amigo_suite.jpg");

        var r450 = new Room(450, RoomType.SUITE, 540.0, false,
                "/images/rooms/hilton_suite.jpg");
        var r480 = new Room(480, RoomType.DOUBLE, 260.0, false,
                "/images/rooms/hilton_double.jpg");
        var r490 = new Room(490, RoomType.SINGLE, 140.0, false,
                "/images/rooms/hilton_single.jpg");

        var r533 = new Room(533, RoomType.SUITE, 590.0, true,
                "/images/rooms/silt_suite.jpg");
        var r563 = new Room(563, RoomType.DOUBLE, 270.0, true,
                "/images/rooms/silt_double.jpg");
        var r579 = new Room(579, RoomType.SINGLE, 160.0, true,
                "/images/rooms/silt_single.jpg");

        var r703 = new Room(703, RoomType.SUITE, 420.0, true,
                "/images/rooms/van_der_suite.jpg");
        var r705 = new Room(705, RoomType.DOUBLE, 200.0, true,
                "/images/rooms/van_der_double.jpg");
        var r709 = new Room(709, RoomType.SINGLE, 110.0, true,
                "/images/rooms/van_der_single.jpg");

        var r811 = new Room(811, RoomType.SUITE, 565.0, false,
                "/images/rooms/pan_pacific_suite.jpg");
        var r839 = new Room(839, RoomType.DOUBLE, 300.0, false,
                "/images/rooms/pan_pacific_double.jpg");
        var r857 = new Room(857, RoomType.SINGLE, 220.0, false,
                "/images/rooms/pan_pacific_suite.jpg");



        // Attach rooms to hotels (many-to-one)
        h1.addRoom(r101);
        h1.addRoom(r102);
        h1.addRoom(r201);

        h2.addRoom(r202);
        h2.addRoom(r301);
        h2.addRoom(r401);

        h3.addRoom(r302);
        h3.addRoom(r402);
        h3.addRoom(r403);

        h4.addRoom(r501);
        h4.addRoom(r502);
        h4.addRoom(r503);

        h5.addRoom(r601);
        h5.addRoom(r602);
        h5.addRoom(r603);

        h6.addRoom(r450);
        h6.addRoom(r480);
        h6.addRoom(r490);

        h7.addRoom(r533);
        h7.addRoom(r563);
        h7.addRoom(r579);

        h8.addRoom(r703);
        h8.addRoom(r705);
        h8.addRoom(r709);

        h9.addRoom(r811);
        h9.addRoom(r839);
        h9.addRoom(r857);



        // Guests (>=5, here is 13 guests data)
        var g1 = new Guest("Billie Wilson", LocalDate.of(1990, 4, 10), "billie.wilson@example.com", true,
                "/images/guests/billie_wilson.jpg");
        var g2 = new Guest("Liam Johnson", LocalDate.of(1985, 12, 3), "liam.johnson@example.com", false,
                "/images/guests/liam_johnson.jpg");
        var g3 = new Guest("Sophia Martinez", LocalDate.of(1992, 9, 18), "sophia.martinez@example.com", true,
                "/images/guests/sophia_martinez.jpg");
        var g4 = new Guest("Ahanyna Saha", LocalDate.of(2002, 11,28), "ahanyna.saha@gmail.com", true,
                "/images/guests/ahanyna_saha.jpg");
        var g5 = new Guest("Olivia Garcia", LocalDate.of(1995, 2, 8), "olivia.garcia@example.com", true,
                "/images/guests/olivia_garcia.jpg");
        var g6 = new Guest("Ethan Brown", LocalDate.of(1991, 6, 15), "ethan.brown@example.com", false,
                "/images/guests/ethan_brown.jpg");
        var g7 = new Guest("Mia Chen", LocalDate.of(1997, 3, 22), "mia.chen@example.com", true,
                "/images/guests/mia_chen.jpg");
        var g8 = new Guest("Alexander Rossi", LocalDate.of(1989, 11, 9), "alex.rossi@example.com", false,
                "/images/guests/alexander_rossi.jpg");
        var g9 = new Guest("Marrison Harri", LocalDate.of(2001, 6, 12), "harri@example.com", true,
                "/images/guests/marrison_harri.jpg");
        var g10 = new Guest("Emma Janssens", LocalDate.of(1995, 4, 10), "emma.janssens@example.com", false,
                "/images/guests/emma_janssens.jpg");
        var g11 = new Guest("Lucas Peeters", LocalDate.of(1988, 11, 21), "lucas.peeters@example.com", true,
                "/images/guests/lucas_peeters.jpg");
        var g12 = new Guest("Kate Claes", LocalDate.of(1992, 7, 15), "kate.claes@example.com", false,
                "/images/guests/kate_claes.jpg");
        var g13 = new Guest("Noah Smith", LocalDate.of(1988, 7, 25), "noah.smith@example.com", false,
                "/images/guests/noah_smith.jpg");



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

        g13.addRoom(r403);



        // Fill public static lists rooms and guests
        hotels.addAll(List.of(h1, h2, h3, h4, h5, h6, h7, h8, h9));
        rooms.addAll(List.of(r101, r102, r201, r202, r301, r302, r401, r402, r403,
                r501, r502, r503, r601, r602, r603, r450, r480, r490, r533, r563, r579, r703,
                r705, r709, r811, r839, r857));
        guests.addAll(List.of(g1, g2, g3, g4, g5, g6, g7, g8, g9, g10, g11, g12, g13));

    }
}