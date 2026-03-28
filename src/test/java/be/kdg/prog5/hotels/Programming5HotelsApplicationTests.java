package be.kdg.prog5.hotels;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")    // use application-test.properties instead of the normal configuration.
class Programming5HotelsApplicationTests {

    @Test
    void contextLoads() {
    }
}