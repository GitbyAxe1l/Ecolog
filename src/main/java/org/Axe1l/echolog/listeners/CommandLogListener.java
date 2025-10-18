package org.Axe1l.echolog.listeners;

import org.Axe1l.echolog.EchoLogPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashMap;
import java.util.Map;

public class CommandLogListener implements Listener {
    private final EchoLogPlugin plugin;
    public CommandLogListener(EchoLogPlugin plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCmd(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        Map<String,String> ctx = new HashMap<>();
        ctx.put("player", p.getName());
        ctx.put("uuid", p.getUniqueId().toString());
        ctx.put("world", p.getWorld().getName());
        ctx.put("x", String.valueOf(p.getLocation().getBlockX()));
        ctx.put("y", String.valueOf(p.getLocation().getBlockY()));
        ctx.put("z", String.valueOf(p.getLocation().getBlockZ()));
        ctx.put("command", e.getMessage().startsWith("/") ? e.getMessage().substring(1) : e.getMessage());
        plugin.dispatcher().log("logs.commands", ctx);
    }
}
