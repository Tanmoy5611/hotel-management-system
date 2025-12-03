package be.kdg.prog3.hotels;

import be.kdg.prog3.hotels.data.*;
import be.kdg.prog3.hotels.business.*;
import be.kdg.prog3.hotels.data.inmemory.InMemoryHotelRepository;
import be.kdg.prog3.hotels.data.inmemory.InMemoryRoomRepository;
import be.kdg.prog3.hotels.presentation.*;
import be.kdg.prog3.hotels.data.DataFactory;

import java.util.Scanner;

public class StartManualDI {
    public static void main(String[] args) {
        DataFactory.seed();  // Load initial data

        // Data layer (repositories)
        HotelRepository hotelRepo = new InMemoryHotelRepository();
        RoomRepository roomRepo = new InMemoryRoomRepository();

        // Business layer (services)
        HotelService hotelService = new HotelServiceImpl(hotelRepo);
        RoomService roomService = new RoomServiceImpl(roomRepo);

        // Presentation layer (console view + presenter)
        MenuView view = new MenuView(new Scanner(System.in));
        MenuPresenter presenter = new MenuPresenter(hotelService, roomService, view);

        presenter.run(); // Run the application


    }
}
