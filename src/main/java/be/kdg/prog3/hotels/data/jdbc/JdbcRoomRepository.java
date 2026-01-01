package be.kdg.prog3.hotels.data.jdbc;
import be.kdg.prog3.hotels.data.HotelRepository;
import be.kdg.prog3.hotels.data.RoomRepository;
import be.kdg.prog3.hotels.domain.Guest;
import be.kdg.prog3.hotels.domain.Hotel;
import be.kdg.prog3.hotels.domain.Room;
import be.kdg.prog3.hotels.domain.RoomType;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Profile("jdbc")
public class JdbcRoomRepository implements RoomRepository {
    private final JdbcClient jdbcClient;
    private final HotelRepository hotelRepository;

    public JdbcRoomRepository(JdbcClient jdbcClient, HotelRepository hotelRepository) {
        this.jdbcClient = jdbcClient;
        this.hotelRepository = hotelRepository;
    }

    // RowMapper converts one row from the rooms table into a Room object, and also loads its related Hotel and Guests
    private Room mapRoom(ResultSet rs, int row) throws SQLException {

        // Create Room from rooms table
        Room room = new Room(
                rs.getInt("number"),
                RoomType.valueOf(rs.getString("type").toUpperCase()),
                rs.getDouble("price_per_night"),
                rs.getBoolean("sea_view"),
                rs.getString("photo_url")
        );

        // Load hotel (Many-to-One) using HotelRepository
        String hotelId = rs.getString("hotel_id");
        Hotel hotel = hotelRepository.findHotelById(hotelId);
        room.setHotel(hotel);

        // Load guests (Many-to-Many)
        var guests = jdbcClient.sql("""
                        SELECT g.* FROM guests g
                        JOIN rooms_guests rg ON g.id = rg.guest_id
                        WHERE rg.room_number = :num
                        """)
                .param("num", room.getNumber())
                .query((grs, grow) -> {
                    Guest g = new Guest(
                            grs.getString("full_name"),
                            grs.getDate("dob").toLocalDate(),
                            grs.getString("email"),
                            grs.getBoolean("vip"),
                            grs.getString("avatar_url")
                    );
                    g.setId(grs.getLong("id"));
                    return g;
                })
                .list();

        guests.forEach(room::addGuest); // Attach guests to room

        return room;
    }

    // Returns all rooms from the database with full relations loaded
    @Override
    public List<Room> findAll() {
        return jdbcClient.sql("SELECT * FROM rooms")
                .query(this::mapRoom)
                .list();
    }

    // Returns a single room by its number or null if not found
    @Override
    public Room findById(int number) {
        return jdbcClient.sql("SELECT * FROM rooms WHERE number = :num")
                .param("num", number)
                .query(this::mapRoom)
                .list()
                .stream()
                .findFirst()
                .orElse(null);
    }

    // Returns all rooms belonging to a specific hotel
    @Override
    public List<Room> findByHotel(String hotelId) {
        return jdbcClient.sql("SELECT * FROM rooms WHERE hotel_id = :hid")
                .param("hid", hotelId)
                .query(this::mapRoom)
                .list();
    }

    // Returns all rooms assigned to a given guest (many-to-many)
    @Override
    public List<Room> findByGuest(long guestId) {
        return jdbcClient.sql("""
                        SELECT r.*
                        FROM rooms r
                        JOIN rooms_guests rg ON r.number = rg.room_number
                        WHERE rg.guest_id = :gid
                        """)
                .param("gid", guestId)
                .query(this::mapRoom)
                .list();
    }

    // Inserts a new room into the database
    @Override
    public Room save(Room room) {
        jdbcClient.sql("""
                        INSERT INTO rooms (number, type, price_per_night, sea_view, photo_url, hotel_id)
                        VALUES (:num, :type, :price, :sea, :photo, :hotel)
                        """)
                .param("num", room.getNumber())
                .param("type", room.getType().name())
                .param("price", room.getPricePerNight())
                .param("sea", room.isSeaView())
                .param("photo", room.getPhotoUrl())
                .param("hotel", room.getHotel().getId())
                .update();

        return room;
    }

    // Deletes a room and its join-table entries
    @Override
    public void delete(int number) {
        // First delete many-to-many links
        jdbcClient.sql("DELETE FROM rooms_guests WHERE room_number = :num")
                .param("num", number)
                .update();

        // Then delete room
        jdbcClient.sql("DELETE FROM rooms WHERE number = :num")
                .param("num", number)
                .update();
    }
}