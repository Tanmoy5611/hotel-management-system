package be.kdg.prog3.hotels.presentation;

import be.kdg.prog3.hotels.domain.Hotel;
import be.kdg.prog3.hotels.domain.Room;
import java.util.List;
import java.util.Scanner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

// Handles all console input/output for the menu
@Profile("console")
@Component
public class MenuView {
    private final Scanner sc;

    // Scanner is injected from Spring (defined as @Bean in main class)
    public MenuView(Scanner sc) {
        this.sc = sc;
    }

    // Shows the main menu options on screen
    public void showMenu() {
        System.out.println("""
                What would you like to do?
                ==========================
                0) Quit
                1) Show all hotels
                2) Show hotels with minimum stars and opened date
                3) Show all rooms
                4) Show rooms with optional filters (type / sea view / max price)
                """);

        System.out.print("Make your choice (0-4): ");
    }

    // Read one line from user input
    public String readLine() {
        return sc.nextLine();
    }

    // Print a list of hotels in console
    public void printHotels(List<Hotel> hotels) {
        hotels.forEach(System.out::println);
    }

    // Print a list of rooms in console
    public void printRooms(List<Room> rooms) {
        rooms.forEach(System.out::println);

    }

    // Prints a simple message to console
    public void print(String msg) {

        System.out.println(msg);
    }
}
