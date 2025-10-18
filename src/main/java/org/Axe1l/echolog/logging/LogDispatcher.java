package org.Axe1l.echolog.logging;

import org.Axe1l.echolog.EchoLogPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class LogDispatcher {

    private final EchoLogPlugin plugin;
    private final FileOutput fileOut;
    private final DiscordWebhookSender discord;
    private final PlaceholderFormatter fmt;

    public LogDispatcher(EchoLogPlugin plugin) {
        this.plugin = plugin;
        this.fileOut = new FileOutput(plugin);
        this.discord = new DiscordWebhookSender(plugin);
        this.fmt = new PlaceholderFormatter(plugin);
    }

    public void reload() {
        // aktuell nichts zu cachen, aber Hook falls du später willst
    }

    /** Allgemeiner Entry-Point für alle Listener */
    public void log(String basePath, Map<String,String> ctx) {
        try {
            FileConfiguration c = plugin.getConfig();

            // 1) enabled?
            if (!c.getBoolean(basePath + ".enabled", true)) return;

            // 2) format auflösen
            String fmtPath = formatPath(basePath);
            String pattern = c.getString(fmtPath, fmt.defaultFormatFor(basePath));
            String line = fmt.render(pattern, ctx);

            // 3) FILE
            if (c.getBoolean("outputs.file.enabled", true)) {
                String folder = c.getString("outputs.file.folder", "logs/echolog");
                fileOut.write(basePath, folder, line);
            }

            // 4) DISCORD
            if (c.getBoolean("outputs.discord.enabled", false)) {
                String specific = c.getString(basePath + ".discord-webhook", "");
                String fallback = c.getString("outputs.discord.default-webhook", "");
                String url = (specific != null && !specific.isBlank()) ? specific : fallback;
                if (url != null && !url.isBlank()) {
                    boolean simplified = c.getBoolean(basePath + ".simplified-message", false);
                    discord.sendAsync(url, line, simplified, basePath, ctx);
                }
            }
        } catch (Exception ex) {
            plugin.getLogger().warning("[EchoLog] Dispatch-Fehler für " + basePath + ": " + ex.getMessage());
        }
    }

    /** Testbutton im GUI */
    public void testFor(String basePath, Player p) {
        Map<String,String> ctx = new HashMap<>();
        ctx.put("player", p.getName());
        ctx.put("uuid", p.getUniqueId().toString());
        ctx.put("message", "This is a test.");
        ctx.put("world", p.getWorld().getName());
        ctx.put("x", String.valueOf(p.getLocation().getBlockX()));
        ctx.put("y", String.valueOf(p.getLocation().getBlockY()));
        ctx.put("z", String.valueOf(p.getLocation().getBlockZ()));
        ctx.put("reason", "—");
        ctx.put("command", "—");
        ctx.put("from", "—");
        ctx.put("to", "—");
        ctx.put("action", "—");
        ctx.put("block", "—");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                log(basePath, ctx);
                p.sendMessage(plugin.lang().prefix() + "§aTest versendet.");
            } catch (Exception ex) {
                p.sendMessage(plugin.lang().prefix() + "§cTest fehlgeschlagen: §7" + ex.getMessage());
                plugin.getLogger().warning("[EchoLog] Test-Dispatch-Error: " + ex.getMessage());
            }
        });
    }

    /** Mappe basePath → format-Pfad */
    private String formatPath(String base) {
        return switch (base) {
            case "logs.chat"              -> "logs.chat.format";
            case "logs.join_quit"         -> "logs.join_quit.join-format"; // legacy fallback
            case "logs.join_quit.join"    -> "logs.join_quit.join-format";
            case "logs.join_quit.quit"    -> "logs.join_quit.quit-format";
            case "logs.death"             -> "logs.death.format";
            case "logs.commands"          -> "logs.commands.format";
            case "logs.teleport"          -> "logs.teleport.format";
            case "logs.block"             -> "logs.block.format";
            default -> base + ".format";
        };
    }
}
