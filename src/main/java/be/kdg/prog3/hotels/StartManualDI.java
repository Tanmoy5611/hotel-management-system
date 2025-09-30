package be.kdg.prog3.hotels;

import be.kdg.prog3.hotels.data.*;
import be.kdg.prog3.hotels.business.*;

import be.kdg.prog3.hotels.presentation.*;
import be.kdg.prog3.hotels.data.DataFactory;

import java.util.Scanner;

public class StartManualDI {
    public static void main(String[] args) {
        DataFactory.seed();

        HotelRepository hotelRepo = new InMemoryHotelRepository();
        RoomRepository roomRepo = new InMemoryRoomRepository();

        HotelService hotelService = new HotelServiceImpl(hotelRepo);
        RoomService roomService = new RoomServiceImpl(roomRepo);

        MenuView view = new MenuView(new Scanner(System.in));
        MenuPresenter presenter = new MenuPresenter(hotelService, roomService, view);

        presenter.run();


    }
}