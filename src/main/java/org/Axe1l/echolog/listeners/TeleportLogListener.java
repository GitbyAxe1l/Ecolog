package org.Axe1l.echolog.listeners;

import org.Axe1l.echolog.EchoLogPlugin;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Map;

public class TeleportLogListener implements Listener {
    private final EchoLogPlugin plugin;
    public TeleportLogListener(EchoLogPlugin plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) {
        var p = e.getPlayer();
        Location from = e.getFrom(), to = e.getTo();
        Map<String,String> ctx = new HashMap<>();
        ctx.put("player", p.getName());
        ctx.put("uuid", p.getUniqueId().toString());
        ctx.put("world", to == null ? from.getWorld().getName() : to.getWorld().getName());
        ctx.put("from", from.getWorld().getName() + " " + from.getBlockX()+"/"+from.getBlockY()+"/"+from.getBlockZ());
        if (to != null)
            ctx.put("to", to.getWorld().getName() + " " + to.getBlockX()+"/"+to.getBlockY()+"/"+to.getBlockZ());
        else
            ctx.put("to", "-");
        plugin.dispatcher().log("logs.teleport", ctx);
    }
}
