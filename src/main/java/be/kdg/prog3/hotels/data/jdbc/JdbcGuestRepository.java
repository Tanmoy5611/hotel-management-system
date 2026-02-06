package be.kdg.prog3.hotels.data.jdbc;
import be.kdg.prog3.hotels.data.GuestRepository;
import be.kdg.prog3.hotels.domain.Guest;
import be.kdg.prog3.hotels.domain.Room;
import be.kdg.prog3.hotels.domain.RoomType;
import be.kdg.prog3.hotels.domain.VIPGuest;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

// JDBC-based repository by using SQL queries and join tables
@Repository
@Profile("jdbc")
public class JdbcGuestRepository implements GuestRepository {
    private final JdbcClient jdbcClient;

    // Constructor injection (Spring provides JdbcClient automatically)
    public JdbcGuestRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // RowMapper (translator between SQL row to Java object) for Guest / VIPGuest (used everywhere)
    private Guest mapGuest(ResultSet rs, int rowNum) throws SQLException {

        String type = rs.getString("guest_type");
        boolean vipFlag = rs.getBoolean("vip");
        double discount = rs.getInt("discount_percentage");

        boolean isVip = "VIP".equalsIgnoreCase(type) || vipFlag || discount > 0;

        Guest guest;

        if (isVip) {
            guest = new VIPGuest(
                    rs.getString("full_name"),
                    rs.getDate("dob").toLocalDate(),
                    rs.getString("email"),
                    true,
                    rs.getString("avatar_url"),
                    discount
            );
        } else {
            guest = new Guest(
                    rs.getString("full_name"),
                    rs.getDate("dob").toLocalDate(),
                    rs.getString("email"),
                    false,
                    rs.getString("avatar_url")
            );
        }

        guest.setId(rs.getLong("id"));
        return guest;
    }


    // Retrieve all guests from H2 DB
    @Override
    public List<Guest> findAll() {
        return jdbcClient.sql("SELECT * FROM guests")
                .query(this::mapGuest)
                .list();
    }

    @Override
    public Guest save(Guest guest) {

        jdbcClient.sql("""
        INSERT INTO guests (full_name, dob, email, vip, avatar_url, guest_type, discount_percentage)
        VALUES (:fullName, :dob, :email, :vip, :avatarUrl, :guestType, :discount)
        """)
                .param("fullName", guest.getFullName())
                .param("dob", guest.getDob())
                .param("email", guest.getEmail())
                .param("vip", guest.isVip())
                .param("avatarUrl", guest.getAvatarUrl())
                .param("guestType", guest instanceof VIPGuest ? "VIP" : "GUEST")
                .param("discount", guest instanceof VIPGuest
                        ? ((VIPGuest) guest).getDiscountPercentage()
                        : 0)
                .update();

        Long id = jdbcClient.sql("SELECT MAX(id) FROM guests")
                .query(Long.class)
                .single();

        guest.setId(id);

        //  NO rooms_guests logic here
        return guest;
    }

    // FIND BY ID (with rooms)
    @Override
    public Guest findById(Long id) {
        // 1) Load guest
        Guest guest = jdbcClient.sql("SELECT * FROM guests WHERE id = :id")
                .param("id", id)
                .query(this::mapGuest)
                .list()
                .stream()
                .findFirst()
                .orElse(null);

        if (guest == null) {
            return null;
        }

        // Load associated rooms from rooms_guests
        List<Room> rooms = jdbcClient.sql("""
        SELECT r.id, r.number, r.type, r.price_per_night, r.sea_view, r.photo_url
        FROM rooms r
        JOIN rooms_guests rg ON r.id = rg.room_id
        WHERE rg.guest_id = :id
        """)
                .param("id", id)
                .query((rs, rowNum) -> {
                    Room room = new Room(
                            rs.getInt("number"),
                            RoomType.valueOf(rs.getString("type")),
                            rs.getDouble("price_per_night"),
                            rs.getBoolean("sea_view"),
                            rs.getString("photo_url"),
                            rs.getString("description")
                    );
                    room.setId(rs.getLong("id"));
                    return room;
                })
                .list();

        guest.getRooms().addAll(rooms);

        return guest;
    }


    // Find by room
    // Find all guests for a given room
    @Override
    public List<Guest> findByRoom(Long roomId) {
        return jdbcClient.sql("""
                SELECT g.*
                FROM guests g
                JOIN rooms_guests rg ON g.id = rg.guest_id
                WHERE rg.room_id = :roomId
                """)
                .param("roomId", roomId)
                .query(this::mapGuest)
                .list();
    }

    // DELETE (remove from join-table first, then remove guest)
    @Override
    public void delete(Long id) {

        // Delete join-table rows first
        jdbcClient.sql("DELETE FROM rooms_guests WHERE guest_id = :id")
                .param("id", id)
                .update();

        // Delete guest
        jdbcClient.sql("DELETE FROM guests WHERE id = :id")
                .param("id", id)
                .update();
    }

    @Override
    public void addGuestToRoom(Long guestId, Long roomId) {
        jdbcClient.sql("""
        INSERT INTO rooms_guests (guest_id, room_id)
        VALUES (:guestId, :roomId)
        """)
                .param("guestId", guestId)
                .param("roomId", roomId)
                .update();
    }
}