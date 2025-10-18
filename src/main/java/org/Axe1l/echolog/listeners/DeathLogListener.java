package org.Axe1l.echolog.listeners;

import org.Axe1l.echolog.EchoLogPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;

public class DeathLogListener implements Listener {
    private final EchoLogPlugin plugin;
    public DeathLogListener(EchoLogPlugin plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        var p = e.getEntity();
        var loc = p.getLocation();
        Map<String,String> ctx = new HashMap<>();
        ctx.put("player", p.getName());
        ctx.put("uuid", p.getUniqueId().toString());
        ctx.put("world", loc.getWorld().getName());
        ctx.put("x", String.valueOf(loc.getBlockX()));
        ctx.put("y", String.valueOf(loc.getBlockY()));
        ctx.put("z", String.valueOf(loc.getBlockZ()));
        ctx.put("reason", e.getDeathMessage() == null ? "" : e.getDeathMessage());
        plugin.dispatcher().log("logs.death", ctx);
    }
}
