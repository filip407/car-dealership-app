package service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService {

    private static AuditService instance;
    private static final String CSV_FILE = "audit.csv";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private AuditService() {}

    public static AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }

    public void log(String actionName) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE, true))) {
            writer.println(actionName + "," + timestamp);
        } catch (IOException e) {
            System.out.println("[AUDIT ERROR] Nu s-a putut scrie in fisierul de audit: " + e.getMessage());
        }
    }
}
