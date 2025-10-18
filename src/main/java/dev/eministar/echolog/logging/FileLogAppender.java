package dev.eministar.echolog.logging;

import dev.eministar.echolog.EchoLogPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileLogAppender {
    private final EchoLogPlugin plugin;

    public FileLogAppender(EchoLogPlugin plugin) {
        this.plugin = plugin;
        ensureFolder();
    }

    private void ensureFolder() {
        FileConfiguration c = plugin.configs().cfg();
        File dir = new File(plugin.getDataFolder(), c.getString("outputs.file.folder", "logs/echolog"));
        if (!dir.exists()) dir.mkdirs();
    }

    public void append(String channel, String line) {
        FileConfiguration c = plugin.configs().cfg();
        File dir = new File(plugin.getDataFolder(), c.getString("outputs.file.folder", "logs/echolog"));
        if (!dir.exists()) dir.mkdirs();

        File f = new File(dir, channel + "-" + date() + ".log");

        // simple size rotation
        long limit = c.getInt("outputs.file.rotate-size-kb", 5120) * 1024L;
        if (f.exists() && f.length() >= limit) {
            File rotated = new File(dir, channel + "-" + date() + "-" + System.currentTimeMillis() + ".log");
            f.renameTo(rotated);
        }

        try (Writer w = new OutputStreamWriter(new FileOutputStream(f, true), StandardCharsets.UTF_8)) {
            w.write(line);
            w.write(System.lineSeparator());
        } catch (IOException e) {
            plugin.getLogger().warning("File append failed: " + e.getMessage());
        }
    }

    private String date() {
        return java.time.LocalDate.now().toString();
    }
}
