package be.kdg.prog3.hotels;

import be.kdg.prog3.hotels.data.DataFactory;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

@SpringBootApplication
public class SpringHotelsApplication {
    public static void main(String[] args) {
        DataFactory.seed();
        SpringApplication.run(SpringHotelsApplication.class, args);
    }

    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }
}