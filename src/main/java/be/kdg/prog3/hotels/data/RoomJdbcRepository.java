package be.kdg.prog3.hotels.data;

import be.kdg.prog3.hotels.domain.Room;
import be.kdg.prog3.hotels.domain.RoomType;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@Profile("jdbc")
public class RoomJdbcRepository implements RoomRepository {
    private final JdbcClient jdbcClient;

    public RoomJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<Room> findAll() {
        return jdbcClient.sql("SELECT * FROM rooms")
                .query((rs, row) -> new Room(
                        rs.getInt("number"),
                        RoomType.valueOf(rs.getString("type").toUpperCase()),
                        rs.getDouble("price_per_night"),
                        rs.getBoolean("sea_view"),
                        rs.getString("photo_url")
                )).list();
    }

    @Override
    public Room save(Room room) {
        jdbcClient.sql("""
                INSERT INTO rooms (number, type, price_per_night, sea_view, photo_url)
                VALUES (:number, :type, :pricePerNight, :seaView, :photoUrl)
                """)
                .param("number", room.getNumber())
                .param("type", room.getType().name())
                .param("pricePerNight", room.getPricePerNight())
                .param("seaView", room.isSeaView())
                .param("photoUrl", room.getPhotoUrl())
                .update();
        return room;
    }
}