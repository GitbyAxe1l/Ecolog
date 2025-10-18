package org.Axe1l.echolog.listeners;

import org.Axe1l.echolog.EchoLogPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashMap;
import java.util.Map;

public class BlockLogListener implements Listener {
    private final EchoLogPlugin plugin;
    public BlockLogListener(EchoLogPlugin plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        var p = e.getPlayer();
        var b = e.getBlockPlaced();
        Map<String,String> ctx = new HashMap<>();
        ctx.put("player", p.getName());
        ctx.put("uuid", p.getUniqueId().toString());
        ctx.put("world", b.getWorld().getName());
        ctx.put("x", String.valueOf(b.getX()));
        ctx.put("y", String.valueOf(b.getY()));
        ctx.put("z", String.valueOf(b.getZ()));
        ctx.put("action", "place");
        ctx.put("block", b.getType().name());
        plugin.dispatcher().log("logs.block", ctx);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        var p = e.getPlayer();
        var b = e.getBlock();
        Map<String,String> ctx = new HashMap<>();
        ctx.put("player", p.getName());
        ctx.put("uuid", p.getUniqueId().toString());
        ctx.put("world", b.getWorld().getName());
        ctx.put("x", String.valueOf(b.getX()));
        ctx.put("y", String.valueOf(b.getY()));
        ctx.put("z", String.valueOf(b.getZ()));
        ctx.put("action", "break");
        ctx.put("block", b.getType().name());
        plugin.dispatcher().log("logs.block", ctx);
    }
}
