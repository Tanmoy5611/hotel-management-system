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

@Repository
@Profile("jdbc")
public class JdbcGuestRepository implements GuestRepository {
    private final JdbcClient jdbcClient;

    // Constructor injection (Spring provides JdbcClient automatically)
    public JdbcGuestRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // RowMapper for Guest / VIPGuest (used everywhere)
    private Guest mapGuest(ResultSet rs, int rowNum) throws SQLException {

        String type = rs.getString("guest_type");
        boolean vipFlag = rs.getBoolean("vip");
        double discount = rs.getInt("discount_percentage");

        boolean isVip = "VIP".equalsIgnoreCase(type) || vipFlag || discount > 0;

        Guest g;

        if (isVip) {
            g = new VIPGuest(
                    rs.getString("full_name"),
                    rs.getDate("dob").toLocalDate(),
                    rs.getString("email"),
                    true,
                    rs.getString("avatar_url"),
                    discount
            );
        } else {
            g = new Guest(
                    rs.getString("full_name"),
                    rs.getDate("dob").toLocalDate(),
                    rs.getString("email"),
                    false,
                    rs.getString("avatar_url")
            );
        }

        g.setId(rs.getLong("id"));
        return g;
    }


    // Retrieve all guests from H2 DB
    @Override
    public List<Guest> findAll() {
        return jdbcClient.sql("SELECT * FROM guests")
                .query(this::mapGuest)
//                .query((rs, rowNum) -> {
//                    Guest g = new Guest(
//                            rs.getString("full_name"),
//                            rs.getDate("dob").toLocalDate(),
//                            rs.getString("email"),
//                            rs.getBoolean("vip"),
//                            rs.getString("avatar_url")
//                    );
//                    g.setId(rs.getLong("id"));
//                    return g;
//                })
                .list();
    }

    // Add a new guest to DB
    @Override
    public Guest save(Guest guest) {

        // Insert into guests table
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
                .param("discount", guest instanceof VIPGuest ? ((VIPGuest) guest).getDiscountPercentage() : 0)
                .update();

        // Get generated id (simple way for H2)
        Long id = jdbcClient.sql("SELECT MAX(id) FROM guests")
                .query(Long.class)
                .single();

        guest.setId(id);

        // Insert rooms for join-table: rooms_guests
        for (Room room : guest.getRooms()) {
            jdbcClient.sql("""
                    INSERT INTO rooms_guests (guest_id, room_number)
                    VALUES (:guestId, :roomNumber)
                    """)
                    .param("guestId", guest.getId())
                    .param("roomNumber", room.getNumber())
                    .update();
        }

        return guest;
    }


    @Override
    public Guest findById(long id) {
        // 1) Load guest
        Guest guest = jdbcClient.sql("SELECT * FROM guests WHERE id = :id")
                .param("id", id)
                .query(this::mapGuest)
//                .query((rs, rowNum) -> {
//                    Guest g = new Guest(
//                            rs.getString("full_name"),
//                            rs.getDate("dob").toLocalDate(),
//                            rs.getString("email"),
//                            rs.getBoolean("vip"),
//                            rs.getString("avatar_url")
//                    );
//                    g.setId(rs.getLong("id"));
//                    return g;
//                })
                .list()
                .stream()
                .findFirst()
                .orElse(null);

        if (guest == null) {
            return null;
        }

        // Load associated rooms from rooms_guests
        List<Room> rooms = jdbcClient.sql("""
                SELECT r.number, r.type, r.price_per_night, r.sea_view, r.photo_url
                FROM rooms r
                JOIN rooms_guests rg ON r.number = rg.room_number
                WHERE rg.guest_id = :id
                """)
                .param("id", id)
                .query((rs, rowNum) -> new Room(
                        rs.getInt("number"),
                        RoomType.valueOf(rs.getString("type")),
                        rs.getDouble("price_per_night"),
                        rs.getBoolean("sea_view"),
                        rs.getString("photo_url")
                ))
                .list();

        rooms.forEach(guest::addRoom);

        return guest;
    }



    // Find by room
    // Find all guests for a given room
    // -------------------------------------------------------------
    @Override
    public List<Guest> findByRoom(int roomNumber) {
        return jdbcClient.sql("""
                SELECT g.*
                FROM guests g
                JOIN rooms_guests rg ON g.id = rg.guest_id
                WHERE rg.room_number = :num
                """)
                .param("num", roomNumber)
                .query(this::mapGuest)
//                .query((rs, rowNum) -> {
//                    var guest = new Guest(
//                            rs.getString("full_name"),
//                            rs.getDate("dob").toLocalDate(),
//                            rs.getString("email"),
//                            rs.getBoolean("vip"),
//                            rs.getString("avatar_url")
//                    );
//                    guest.setId(rs.getLong("id"));   // important: set DB id
//                    return guest;
//                })
                .list();

    }

    // DELETE (remove from join-table first, then remove guest)
    @Override
    public void delete(long id) {

        // Delete join-table rows first
        jdbcClient.sql("DELETE FROM rooms_guests WHERE guest_id = :id")
                .param("id", id)
                .update();

        // Delete guest
        jdbcClient.sql("DELETE FROM guests WHERE id = :id")
                .param("id", id)
                .update();
    }
}