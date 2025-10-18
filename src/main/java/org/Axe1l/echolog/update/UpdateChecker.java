package org.Axe1l.echolog.update;

import org.Axe1l.echolog.EchoLogPlugin;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UpdateChecker {
    private static final String FEED_URL = "https://star-dev.xyz/echolog/version.txt";
    private final EchoLogPlugin plugin;

    private volatile String latestVersion = null;
    private volatile boolean updateAvailable = false;

    public UpdateChecker(EchoLogPlugin plugin) {
        this.plugin = plugin;
    }

    public void startAsyncCheck() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> checkNow(false));
    }

    /** Force = true → immer Konsole informieren, auch wenn up-to-date */
    public void checkNow(boolean forceLog) {
        String current = plugin.getDescription().getVersion();
        String latest = fetchLatest();
        if (latest == null || latest.isBlank()) {
            if (forceLog) plugin.getLogger().warning("[Update] Konnte neueste Version nicht abrufen.");
            return;
        }
        latestVersion = latest.trim();
        int cmp = compareVersions(sanitize(current), sanitize(latestVersion));
        updateAvailable = (cmp < 0);

        if (updateAvailable) {
            logConsoleBox(current, latestVersion, getDownloadUrl());
        } else if (forceLog) {
            plugin.getLogger().info("[Update] EchoLog ist aktuell. Version " + current);
        }
    }

    public boolean isUpdateAvailable() { return updateAvailable; }
    public String getLatestVersion() { return latestVersion; }

    public String getDownloadUrl() {
        // konfigurierbar, default auf Projektseite
        return plugin.getConfig().getString("updates.download-url", "https://star-dev.xyz/echolog");
    }

    private String fetchLatest() {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(new URL(FEED_URL).openStream(), StandardCharsets.UTF_8))) {
            return in.readLine();
        } catch (Exception e) {
            plugin.getLogger().warning("[Update] Fetch fehlgeschlagen: " + e.getMessage());
            return null;
        }
    }

    private String sanitize(String v) {
        // 1.2.3-SNAPSHOT → 1.2.3-0 damit < Release
        String s = v.trim();
        s = s.replace("_", "-");
        if (s.toLowerCase().contains("snapshot")) s = s.replaceAll("(?i)-?snapshot", "-0");
        return s;
    }

    /** Vergleich: return <0 wenn a<b, 0 wenn =, >0 wenn a>b */
    private int compareVersions(String a, String b) {
        String[] aa = a.split("[^0-9A-Za-z]+");
        String[] bb = b.split("[^0-9A-Za-z]+");
        int n = Math.max(aa.length, bb.length);
        for (int i = 0; i < n; i++) {
            String x = i < aa.length ? aa[i] : "0";
            String y = i < bb.length ? bb[i] : "0";
            int cmp = comparePart(x, y);
            if (cmp != 0) return cmp;
        }
        return 0;
    }

    private int comparePart(String x, String y) {
        boolean nx = x.matches("\\d+");
        boolean ny = y.matches("\\d+");
        if (nx && ny) {
            long ix = Long.parseLong(x), iy = Long.parseLong(y);
            return Long.compare(ix, iy);
        }
        return x.compareToIgnoreCase(y);
    }

    private void logConsoleBox(String current, String latest, String url) {
        String sep = "§8──────────────────────────────────────────────";
        Bukkit.getConsoleSender().sendMessage(sep);
        Bukkit.getConsoleSender().sendMessage(" §bEcho§3Log §7Update verfügbar!");
        Bukkit.getConsoleSender().sendMessage(" §7Aktuell : §f" + current);
        Bukkit.getConsoleSender().sendMessage(" §7Neu     : §a" + latest);
        Bukkit.getConsoleSender().sendMessage(" §7Download: §b" + url);
        Bukkit.getConsoleSender().sendMessage(sep);
    }
}
