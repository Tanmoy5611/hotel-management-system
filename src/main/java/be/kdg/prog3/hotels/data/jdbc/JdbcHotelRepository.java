package be.kdg.prog3.hotels.data.jdbc;
import be.kdg.prog3.hotels.data.HotelRepository;
import be.kdg.prog3.hotels.domain.Hotel;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import java.util.List;

// This talks directly to the PostgreSQL database using SQL queries.
@Repository
@Profile("jdbc")
public class JdbcHotelRepository implements HotelRepository {
    // Spring's helper for executing SQL queries with named parameters
    private final JdbcClient jdbcClient;

    // Constructor injection of JdbcClient
    public JdbcHotelRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // Find ALL hotels from the "hotels" table.
    @Override
    public List<Hotel> findAll() {
        return jdbcClient.sql("SELECT * FROM hotels")
                // query: map each ResultSet row → new Hotel(...)
                .query((rs, row) -> new Hotel(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("city"),
                        rs.getString("country"),
                        rs.getDate("opened_on").toLocalDate(),
                        rs.getInt("stars"),
                        rs.getBoolean("has_spa"),
                        rs.getString("image_url"),
                        rs.getString("description")
                )).list();   // convert to List<Hotel>
    }

    // Save new hotel to the database by Using an INSERT statement with named parameters
    @Override
    public Hotel save(Hotel hotel) {
        jdbcClient.sql("""
                        INSERT INTO hotels(id, name, city, country, opened_on, stars, has_spa, image_url)
                        VALUES(:id, :name, :city, :country, :opened_on, :stars, :spa, :image)
                        """)
                // Set the named parameters from the Hotel object
                .param("id", hotel.getId())
                .param("name", hotel.getName())
                .param("city", hotel.getCity())
                .param("country", hotel.getCountry())
                .param("opened_on", hotel.getOpenedOn())
                .param("stars", hotel.getStars())
                .param("spa", hotel.isHasSpa())
                .param("image", hotel.getImageUrl())
                .param("description", hotel.getDescription())
                .update();   // Execute the INSERT statement

        return hotel;   // return the same object (no generated id here)
    }

    //  Find a single hotel by its id (primary key).
    @Override
    public Hotel findHotelById(String id) {
        return jdbcClient.sql("SELECT * FROM hotels WHERE id = :id")
                .param("id", id)
                .query((rs, row) -> new Hotel(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("city"),
                        rs.getString("country"),
                        rs.getDate("opened_on").toLocalDate(),
                        rs.getInt("stars"),
                        rs.getBoolean("has_spa"),
                        rs.getString("image_url"),
                        rs.getString("description")
                ))
                .list()                     // get List<Hotel>
                .stream()
                .findFirst()               // pick first if exists
                .orElse(null);       // else return null
    }

    // Delete a hotel and its rooms
    @Override
    public void delete(String id) {
        // Delete all rooms belonging to the hotel (FK safety)
        jdbcClient.sql("DELETE FROM rooms WHERE hotel_id = :id")
                .param("id", id)
                .update();

        // Delete the hotel row itself
        jdbcClient.sql("DELETE FROM hotels WHERE id = :id")
                .param("id", id)
                .update();
    }
}