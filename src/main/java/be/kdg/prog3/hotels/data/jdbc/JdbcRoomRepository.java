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
                rs.getString("photo_url"),
                rs.getString("description")
        );

        // set generated ID
        room.setId(rs.getLong("id"));

        // Load hotel (Many-to-One) using HotelRepository
        String hotelId = rs.getString("hotel_id");
        Hotel hotel = hotelRepository.findHotelById(hotelId);
        if (hotel == null) {
            throw new IllegalStateException("Hotel not found for room " + room.getId());
        }
        room.setHotel(hotel);

        // Load guests (Many-to-Many)
        List<Guest> guests = jdbcClient.sql("""
        SELECT g.* 
        FROM guests g
        JOIN rooms_guests rg ON g.id = rg.guest_id
        WHERE rg.room_id = :roomId
        """)
                .param("roomId", room.getId())
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

        room.getGuests().addAll(guests);


        // guests.forEach(room::addGuest); // Attach guests to room

        return room;
    }

    // Returns all rooms from the database with full relations loaded
    @Override
    public List<Room> findAll() {
        return jdbcClient.sql("SELECT * FROM rooms")
                .query(this::mapRoom)
                .list();
    }

    // Returns a single room by its id or null if not found
    @Override
    public Room findById(Long id) {
        return jdbcClient.sql("SELECT * FROM rooms WHERE id = :id")
                .param("id", id)
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
    public List<Room> findByGuest(Long guestId) {
        return jdbcClient.sql("""
                        SELECT r.*
                        FROM rooms r
                        JOIN rooms_guests rg ON r.id = rg.room_id
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
                        INSERT INTO rooms (number, type, price_per_night, sea_view, photo_url, description, hotel_id)
                        VALUES (:num, :type, :price, :sea, :photo, :description, :hotel)
                        """)
                .param("num", room.getNumber())
                .param("type", room.getType().name())
                .param("price", room.getPricePerNight())
                .param("sea", room.isSeaView())
                .param("photo", room.getPhotoUrl())
                .param("description", room.getDescription())
                .param("hotel", room.getHotel() != null ? room.getHotel().getId() : null)
                .update();

        // fetch generated ID
        Long id = jdbcClient.sql("SELECT MAX(id) FROM rooms")
                .query(Long.class)
                .single();

        room.setId(id);

        return room;
    }

    // Deletes a room and its join-table entries
    @Override
    public void delete(Long id) {
        // First delete many-to-many links
        jdbcClient.sql("DELETE FROM rooms_guests WHERE room_id = :id")
                .param("id", id)
                .update();

        // Then delete room
        jdbcClient.sql("DELETE FROM rooms WHERE id = :id")
                .param("id", id)
                .update();
    }
}