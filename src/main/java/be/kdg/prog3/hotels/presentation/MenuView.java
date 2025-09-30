package be.kdg.prog3.hotels.presentation;

import be.kdg.prog3.hotels.domain.Hotel;
import be.kdg.prog3.hotels.domain.Room;

import java.util.List;
import java.util.Scanner;

import org.springframework.stereotype.Component;

@Component
public class MenuView {
    private final Scanner sc;

    public MenuView(Scanner sc) {
        this.sc = sc;

    }

    public void showMenu() {
        System.out.println("""
            What would you like to do?
            ==========================
            0) Quit
            1) Show all hotels
            2) Show hotels with minimum stars and opened date
            3) Show all rooms
            4) Show rooms with optional filters (type / seaView / max price)
            """);

        System.out.print("Choice (0-4): ");

    }


    public String readLine() {
        return sc.nextLine();
    }

    public void printHotels(List<Hotel> hotels) {
        hotels.forEach(System.out::println);
    }

    public void printRooms(List<Room> rooms) {
        rooms.forEach(System.out::println);

    }

    public void print(String msg) {

        System.out.println(msg);
    }
}
