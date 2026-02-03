package com.Anudip.HotelBooking.entity;

import com.Anudip.HotelBooking.entity.HibernateUtil;
import com.Anudip.HotelBooking.entity.Room;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Scanner;

public class HotelBookingApp {

    public static void main(String[] args) {

        SessionFactory factory = HibernateUtil.getSessionFactory();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== HOTEL BOOKING SYSTEM =====");
            System.out.println("1. Add Room");
            System.out.println("2. View All Rooms");
            System.out.println("3. Update Room Price");
            System.out.println("4. Delete Room");
            System.out.println("5. Search Room by Type");
            System.out.println("6. Sort Rooms by Price (Low → High)");
            System.out.println("7. Sort Rooms by Price (High → Low)");
            System.out.println("8. Show Hotel Statistics");
            System.out.println("9. Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    addRoom(factory, sc);
                    break;

                case 2:
                    viewAllRooms(factory);
                    break;

                case 3:
                    updateRoomPrice(factory, sc);
                    break;

                case 4:
                    deleteRoom(factory, sc);
                    break;

                case 5:
                    searchRoomByType(factory, sc);
                    break;

                case 6:
                    sortRoomsByPrice(factory, true);
                    break;

                case 7:
                    sortRoomsByPrice(factory, false);
                    break;

                case 8:
                    showHotelStats(factory);
                    break;

                case 9:
                    System.out.println("👋 Thank you! Exiting Hotel Booking System.");
                    factory.close();
                    return;

                default:
                    System.out.println("❌ Invalid choice! Try again.");
            }
        }
    }

    // ------------------ 1️⃣ Add Room ------------------
    private static void addRoom(SessionFactory factory, Scanner sc) {

        System.out.print("Enter Room Type (Single/Double/Deluxe): ");
        String type = sc.nextLine();

        System.out.print("Enter Price Per Night: ");
        double price = sc.nextDouble();

        System.out.print("Enter Total Rooms Available: ");
        int count = sc.nextInt();
        sc.nextLine();

        Room room = new Room(type, price, count);

        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(room);
            tx.commit();
            System.out.println("✅ Room added successfully!");
        }
    }

    // ------------------ 2️⃣ View All Rooms ------------------
    private static void viewAllRooms(SessionFactory factory) {

        try (Session session = factory.openSession()) {
            List<Room> rooms = session.createQuery("from Room", Room.class).list();

            if (rooms.isEmpty())
                System.out.println("⚠️ No rooms available!");
            else
                rooms.forEach(System.out::println);
        }
    }

    // ------------------ 3️⃣ Update Room Price ------------------
    private static void updateRoomPrice(SessionFactory factory, Scanner sc) {

        System.out.print("Enter Room ID: ");
        int id = sc.nextInt();

        System.out.print("Enter New Price Per Night: ");
        double price = sc.nextDouble();
        sc.nextLine();

        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();

            Room room = session.get(Room.class, id);
            if (room != null) {
                room.setPricePerNight(price);
                session.merge(room);
                tx.commit();
                System.out.println("✅ Room price updated!");
            } else {
                System.out.println("⚠️ Room not found!");
            }
        }
    }

    // ------------------ 4️⃣ Delete Room ------------------
    private static void deleteRoom(SessionFactory factory, Scanner sc) {

        System.out.print("Enter Room ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();

            Room room = session.get(Room.class, id);
            if (room != null) {
                session.remove(room);
                tx.commit();
                System.out.println("✅ Room deleted successfully!");
            } else {
                System.out.println("⚠️ Room not found!");
            }
        }
    }

    // ------------------ 5️⃣ Search Room by Type ------------------
    private static void searchRoomByType(SessionFactory factory, Scanner sc) {

        System.out.print("Enter Room Type keyword: ");
        String key = sc.nextLine();

        try (Session session = factory.openSession()) {
            List<Room> rooms = session.createQuery(
                            "from Room where roomType like :key", Room.class)
                    .setParameter("key", "%" + key + "%")
                    .list();

            if (rooms.isEmpty())
                System.out.println("⚠️ No matching rooms found!");
            else
                rooms.forEach(System.out::println);
        }
    }

    // ------------------ 6️⃣ Sort Rooms by Price ------------------
    private static void sortRoomsByPrice(SessionFactory factory, boolean asc) {

        String order = asc ? "asc" : "desc";

        try (Session session = factory.openSession()) {
            List<Room> rooms = session.createQuery(
                            "from Room order by pricePerNight " + order, Room.class)
                    .list();

            if (rooms.isEmpty())
                System.out.println("⚠️ No rooms to display!");
            else
                rooms.forEach(System.out::println);
        }
    }

    // ------------------ 7️⃣ Hotel Statistics ------------------
    private static void showHotelStats(SessionFactory factory) {

        try (Session session = factory.openSession()) {
            Object[] stats = session.createQuery(
                    "select count(*), avg(pricePerNight), sum(pricePerNight * totalRooms) from Room",
                    Object[].class).uniqueResult();

            System.out.println("\n===== HOTEL STATISTICS =====");
            System.out.println("Total Room Types : " + stats[0]);
            System.out.println("Average Price    : ₹" + stats[1]);
            System.out.println("Total Room Value : ₹" + stats[2]);
        }
    }
}
