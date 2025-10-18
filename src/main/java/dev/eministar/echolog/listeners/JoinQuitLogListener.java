package dev.eministar.echolog.listeners;

import dev.eministar.echolog.EchoLogPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class JoinQuitLogListener implements Listener {
    private final EchoLogPlugin plugin;
    public JoinQuitLogListener(EchoLogPlugin plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        var p = e.getPlayer();
        var loc = p.getLocation();
        Map<String,String> ctx = new HashMap<>();
        ctx.put("player", p.getName());
        ctx.put("uuid", p.getUniqueId().toString());
        ctx.put("world", loc.getWorld().getName());
        ctx.put("x", String.valueOf(loc.getBlockX()));
        ctx.put("y", String.valueOf(loc.getBlockY()));
        ctx.put("z", String.valueOf(loc.getBlockZ()));
        // eigenes BasePath für Join, damit join-format greift
        plugin.dispatcher().log("logs.join_quit.join", ctx);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        var p = e.getPlayer();
        var loc = p.getLocation();
        Map<String,String> ctx = new HashMap<>();
        ctx.put("player", p.getName());
        ctx.put("uuid", p.getUniqueId().toString());
        ctx.put("world", loc.getWorld().getName());
        ctx.put("x", String.valueOf(loc.getBlockX()));
        ctx.put("y", String.valueOf(loc.getBlockY()));
        ctx.put("z", String.valueOf(loc.getBlockZ()));
        // eigenes BasePath für Quit, damit quit-format greift
        plugin.dispatcher().log("logs.join_quit.quit", ctx);
    }
}
