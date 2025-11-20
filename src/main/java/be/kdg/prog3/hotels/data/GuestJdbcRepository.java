package be.kdg.prog3.hotels.data;

import be.kdg.prog3.hotels.domain.Guest;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile("jdbc")
public class GuestJdbcRepository implements GuestRepository {
    private final JdbcClient jdbcClient;

    // Constructor injection (Spring provides JdbcClient automatically)
    public GuestJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    //
    // Retrieve all guests from H2 DB
    @Override
    public List<Guest> findAll() {
        return jdbcClient.sql("SELECT * FROM guests")
                .query((rs, rowNum) -> new Guest(
                        rs.getString("full_name"),               // fullName
                        rs.getDate("dob").toLocalDate(),         // dob
                        rs.getString("email"),                   // email
                        rs.getBoolean("vip"),                    // vip
                        rs.getString("avatar_url")               // avatarUrl
                ))
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
}