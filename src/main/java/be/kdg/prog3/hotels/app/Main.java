package be.kdg.prog3.hotels.app;

import be.kdg.prog3.hotels.data.DataFactory;
import be.kdg.prog3.hotels.domain.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        DataFactory.seed();  // seed method to load dataset

        // take input from user
        Scanner sc = new Scanner(System.in);

        // using while infinite loop (Menu Loop) to allow user to exit the program
        while (true) {
            System.out.println("""
                    What would you like to do?
                    ==========================
                    0) Quit
                    1) Show all hotels
                    2) Show hotels with minimum stars
                    3) Show all rooms
                    4) Show rooms with optional filters (type / seaView / max price)
                    """);

            System.out.print("Make your choice (0-4):  ");
            String choice = sc.nextLine();

            switch (choice) {
                case "0" -> {
                    System.out.println("Bye!!");
                    return;
                }
                case "1" -> showAllHotels();
                case "2" -> showHotelsByMinStars(sc);
                case "3" -> showAllRooms();
                case "4" -> showRoomsWithOptionalFilters(sc);
                default -> System.out.println("Invalid choice.");

            }
            System.out.println();
        }
    }

    // 1) method to show all hotels
    private static void showAllHotels() {
        // first entity = Hotel (but hotels are reachable via rooms hotel)
        // so derive distinct(avoid duplicates) hotels from rooms list
        DataFactory.rooms.stream()
                .map(Room::getHotel)
                .filter(Objects::nonNull)
                .distinct()
                .forEach(System.out::println);
    }

    // 2) method to show hotels by two mandatory criterion (int stars) and optional (LocalDate openedOn)
    private static void showHotelsByMinStars(Scanner sc) {
        System.out.print("Minimum stars (1-5): ");
        int minStars = Integer.parseInt(sc.nextLine());

        System.out.print("Opened after (yyyy-mm-dd) or empty: ");
        String dateIn = sc.nextLine().trim();

        DataFactory.rooms.stream()
                .map(Room::getHotel)
                .filter(Objects::nonNull)
                .filter(hotel -> hotel.getStars() >= minStars)
                .filter(h -> {
                    if (dateIn.isEmpty()) return true;
                    try {
                        LocalDate filterDate = LocalDate.parse(dateIn);  // parses yyyy-mm-dd
                        return h.getOpenedOn().isAfter(filterDate);
                    } catch (Exception e) {
                        return true;  // ignore invalid input
                    }
                })
                .distinct()
                .forEach(System.out::println);
    }

    // 3) method to show all rooms
    private static void showAllRooms() {
        System.out.print("\nAll rooms\n = ");
        DataFactory.rooms.stream()
                .forEach(System.out::println);
    }

    /**
     * 4) method to show rooms with 2 optional criteria
     *  support 3 optional criteria:
     *   - type (enum)
     *   - seaView (boolean)
     *   - maxPrice (double)
     * Any can be left blank( means don't filter).
     */

    private static void showRoomsWithOptionalFilters(Scanner sc) {
        System.out.print("Type (SINGLE/DOUBLE/SUITE) or empty: ");
        String typeIn = sc.nextLine().trim();

        System.out.print("Sea view? (true/false) or empty: ");
        String seaIn = sc.nextLine().trim();

        System.out.print("Max price (e.g. 150) or empty: ");
        String priceIn = sc.nextLine().trim();


        DataFactory.rooms.stream()
                .filter(r -> typeIn.isEmpty() || r.getType().name().equalsIgnoreCase(typeIn))
                .filter(r -> seaIn.isEmpty() || r.isSeaView() == Boolean.parseBoolean(seaIn))
                .filter(r -> {
                    if (priceIn.isEmpty()) return true;
                    try {
                        double max = Double.parseDouble(priceIn);
                        return r.getPricePerNight() <= max;
                    } catch (NumberFormatException e) {
                        return false;  // ignore invalid input - don't filter by price
                    }
                })
                .forEach(System.out::println);



    }
}