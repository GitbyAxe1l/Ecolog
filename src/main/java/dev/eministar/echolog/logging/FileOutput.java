package dev.eministar.echolog.logging;

import dev.eministar.echolog.EchoLogPlugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;

public class FileOutput {
    private final EchoLogPlugin plugin;

    public FileOutput(EchoLogPlugin plugin) {
        this.plugin = plugin;
    }

    public void write(String basePath, String rootFolder, String line) {
        try {
            String type = basePath.startsWith("logs.") ? basePath.substring(5) : basePath;
            String date = LocalDate.now().toString(); // yyyy-MM-dd
            Path dir = Path.of(rootFolder, date);
            Files.createDirectories(dir);
            Path file = dir.resolve(type + ".log");
            Files.writeString(file, line + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    Files.exists(file) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        } catch (IOException e) {
            plugin.getLogger().warning("[EchoLog] File write failed: " + e.getMessage());
        }
    }
}
