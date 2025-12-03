package be.kdg.prog3.hotels.data.jdbc;

import be.kdg.prog3.hotels.data.GuestRepository;
import be.kdg.prog3.hotels.domain.Guest;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile("jdbc")
public class JdbcGuestRepository implements GuestRepository {
    private final JdbcClient jdbcClient;

    // Constructor injection (Spring provides JdbcClient automatically)
    public JdbcGuestRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // Retrieve all guests from H2 DB
    @Override
    public List<Guest> findAll() {
        return jdbcClient.sql("SELECT * FROM guests")
                .query((rs, rowNum) -> {
                    Guest g = new Guest(
                            rs.getString("full_name"),
                            rs.getDate("dob").toLocalDate(),
                            rs.getString("email"),
                            rs.getBoolean("vip"),
                            rs.getString("avatar_url")
                    );
                    g.setId(rs.getLong("id"));
                    return g;
                })
                .list();
    }

    // Add a new guest to DB
    @Override
    public Guest save(Guest guest) {
        jdbcClient.sql("""
                        INSERT INTO guests (full_name, dob, email, vip, avatar_url)
                        VALUES (:fullName, :dob, :email, :vip, :avatarUrl)
                        """)
                .param("fullName", guest.getFullName())
                .param("dob", guest.getDob())
                .param("email", guest.getEmail())
                .param("vip", guest.isVip())
                .param("avatarUrl", guest.getAvatarUrl())
                .update();
        return guest;
    }

    @Override
    public Guest findById(long id) {
        return jdbcClient.sql("SELECT * FROM guests WHERE id = :id")
                .param("id", id)
                .query((rs, rowNum) -> {
                    Guest g = new Guest(
                            rs.getString("full_name"),
                            rs.getDate("dob").toLocalDate(),
                            rs.getString("email"),
                            rs.getBoolean("vip"),
                            rs.getString("avatar_url")
                    );
                    g.setId(rs.getLong("id"));
                    return g;
                })
                .list()
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Guest> findByRoom(int roomNumber) {
        return jdbcClient.sql("""
                        SELECT g.*
                        FROM guests g
                        JOIN rooms_guests rg ON g.id = rg.guest_id
                        WHERE rg.room_number = :num
                        """)
                .param("num", roomNumber)
                .query((rs, rowNum) -> {
                    var guest = new Guest(
                            rs.getString("full_name"),
                            rs.getDate("dob").toLocalDate(),
                            rs.getString("email"),
                            rs.getBoolean("vip"),
                            rs.getString("avatar_url")
                    );
                    guest.setId(rs.getLong("id"));   // important: set DB id
                    return guest;
                })
                .list();
    }

    @Override
    public void delete(long id) {
        // Delete cross table first
        jdbcClient.sql("DELETE FROM rooms_guests WHERE guest_id = :id")
                .param("id", id)
                .update();

        // Then delete the guest
        jdbcClient.sql("DELETE FROM guests WHERE id = :id")
                .param("id", id)
                .update();
    }
}