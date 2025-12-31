package be.kdg.prog3.hotels;
import be.kdg.prog3.hotels.data.DataFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import java.util.Scanner;

@SpringBootApplication
public class SpringHotelsApplication {
    // main method that starts the entire Spring Boot app
    public static void main(String[] args) {
        SpringApplication.run(SpringHotelsApplication.class, args);   // Start Spring Boot framework
    }

    @Bean
    @Profile("inmemory")
    CommandLineRunner seedInMemoryData() {
        // Seed data Only for in-memory profile
        return args -> DataFactory.seed();
    }

    // Create Scanner bean so it can be injected anywhere in the project if needed
    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }
}

/* TODO: please run the current app and check the following URLs:
         http://localhost:8080/hotels
         http://localhost:8080/rooms
         http://localhost:8080/hotels/add
         http://localhost:8080/rooms/add   */