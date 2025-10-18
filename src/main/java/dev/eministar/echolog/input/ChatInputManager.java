package dev.eministar.echolog.input;

import dev.eministar.echolog.EchoLogPlugin;
import dev.eministar.echolog.util.SoundFX;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ChatInputManager implements Listener {
    private final EchoLogPlugin plugin;
    private static final long TIMEOUT_MS = 30_000L;

    private record Pending(Consumer<String> cb, long expiresAt, String kind) {}
    private final Map<UUID, Pending> waiting = new ConcurrentHashMap<>();

    public ChatInputManager(EchoLogPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        // Periodic cleanup
        Bukkit.getScheduler().runTaskTimer(plugin, this::cleanup, 20L, 20L);
    }

    public void request(Player p, String kind, Consumer<String> callback) {
        waiting.put(p.getUniqueId(), new Pending(callback, System.currentTimeMillis() + TIMEOUT_MS, kind));
    }

    public boolean isWaiting(Player p) { return waiting.containsKey(p.getUniqueId()); }

    private void cleanup() {
        long now = System.currentTimeMillis();
        waiting.entrySet().removeIf(e -> {
            boolean expired = e.getValue().expiresAt < now;
            if (expired) {
                Player p = Bukkit.getPlayer(e.getKey());
                if (p != null) {
                    SoundFX.error(p);
                    p.sendMessage("§7Eingabe für §f"+e.getValue().kind+" §7abgelaufen (§c30s Timeout§7).");
                }
            }
            return expired;
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onChat(AsyncPlayerChatEvent e) {
        Pending pend = waiting.remove(e.getPlayer().getUniqueId());
        if (pend == null) return;

        e.setCancelled(true);
        String msg = e.getMessage().trim();

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (msg.equalsIgnoreCase("cancel") || msg.equalsIgnoreCase("abbrechen")) {
                SoundFX.error(e.getPlayer());
                e.getPlayer().sendMessage("§7Eingabe §cabgebrochen§7.");
                return;
            }
            // wenn es um Webhook geht → validieren
            if (pend.kind.equalsIgnoreCase("webhook") && !isLikelyValidWebhook(msg)) {
                SoundFX.error(e.getPlayer());
                e.getPlayer().sendMessage("§cUngültige URL.§7 Erwartet z. B. §fhttps://discord.com/api/webhooks/…");
                // Zurück in Wartemodus setzen, damit der User neu senden kann:
                waiting.put(e.getPlayer().getUniqueId(), pend);
                return;
            }
            try {
                pend.cb.accept(msg);
                SoundFX.success(e.getPlayer());
            } catch (Exception ex) {
                SoundFX.error(e.getPlayer());
                e.getPlayer().sendMessage("§cFehler beim Verarbeiten: §7"+ex.getMessage());
            }
        });
    }

    /** Minimal-Validierung für Webhook/HTTPS-URLs */
    private boolean isLikelyValidWebhook(String url) {
        if (!url.startsWith("http")) return false;
        try {
            URI u = URI.create(url);
            if (!"https".equalsIgnoreCase(u.getScheme())) return false;
            String host = u.getHost() == null ? "" : u.getHost().toLowerCase();
            if (host.contains("discord.com") || host.contains("discordapp.com")) return true;
            // not zwingend Discord – erlaub allgemein https-URLs mit “/”
            return url.length() > 15 && url.contains("/");
        } catch (Exception e) {
            return false;
        }
    }
}
