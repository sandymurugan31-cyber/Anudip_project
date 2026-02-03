package com.Anudip.HotelBooking.entity;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.HashMap;
import java.util.Map;

public class HibernateUtil {

    private static SessionFactory factory;

    static {
        try {
            // ✅ FIX: Use Map<String, Object> instead of Map<String, String>
            Map<String, Object> settings = new HashMap<>();

            settings.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
            settings.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/hotel_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
            settings.put("hibernate.connection.username", "root");
            settings.put("hibernate.connection.password", "admin123");
            settings.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
            settings.put("hibernate.hbm2ddl.auto", "update");  // auto create/update tables
            settings.put("hibernate.show_sql", "true");
            settings.put("hibernate.format_sql", "true");

            // ✅ Build the StandardServiceRegistry with the correct Map type
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .applySettings(settings)
                    .build();

            // ✅ Register annotated classes
            MetadataSources sources = new MetadataSources(registry)
                    .addAnnotatedClass(Room.class);

            Metadata metadata = sources.getMetadataBuilder().build();
            factory = metadata.getSessionFactoryBuilder().build();

            System.out.println("✅ Hibernate SessionFactory created successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError("❌ Hibernate initialization failed: " + e.getMessage());
        }
    }

    public static SessionFactory getSessionFactory() {
        return factory;
    }
}

