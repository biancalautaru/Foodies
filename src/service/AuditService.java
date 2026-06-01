package service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class AuditService {
    private static final String FILE = "logs/audit.csv";

    private AuditService() {
        try {
            Files.createDirectories(Paths.get("logs"));
            if (!Files.exists(Paths.get(FILE))) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(FILE))) {
                    writer.println("action_name,timestamp");
                }
            }
        } catch (IOException e) {
            System.err.println("AuditService: nu s-a putut inițializa fișierul audit.csv: " + e.getMessage());
        }
    }

    private static class Holder {
        private static final AuditService INSTANCE = new AuditService();
    }

    public static AuditService getInstance() {
        return Holder.INSTANCE;
    }

    public void log(String actionName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE, true))) {
            writer.println(actionName + "," + LocalDateTime.now());
        } catch (IOException e) {
            System.err.println("AuditService: nu s-a putut scrie în fișierul audit.csv: " + e.getMessage());
        }
    }
}