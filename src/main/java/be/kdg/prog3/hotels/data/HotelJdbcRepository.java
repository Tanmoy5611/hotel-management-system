package be.kdg.prog3.hotels.data;

import be.kdg.prog3.hotels.domain.Hotel;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@Profile("jdbc")
public class HotelJdbcRepository implements HotelRepository {
    private final JdbcClient jdbcClient;

    public HotelJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<Hotel> findAll() {
        return jdbcClient.sql("SELECT * FROM hotels")
                .query((rs, row) -> new Hotel(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getDate("opened_on").toLocalDate(),
                        rs.getInt("stars"),
                        rs.getBoolean("has_spa"),
                        rs.getString("image_url")
                )).list();
    }

    @Override
    public Hotel save(Hotel hotel) {
        jdbcClient.sql("""
        INSERT INTO hotels(id, name, opened_on, stars, has_spa, image_url)
        VALUES(:id, :name, :opened_on, :stars, :spa, :image)
        """)
                .param("id", hotel.getId())
                .param("name", hotel.getName())
                .param("opened_on", hotel.getOpenedOn())
                .param("stars", hotel.getStars())
                .param("spa", hotel.isHasSpa())
                .param("image", hotel.getImageUrl())
                .update();

        return hotel;
    }
}