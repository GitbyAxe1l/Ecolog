// dev.eministar.echolog.listeners.ChatLogListener
package dev.eministar.echolog.listeners;

import dev.eministar.echolog.EchoLogPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;

public class ChatLogListener implements Listener {
    private final EchoLogPlugin plugin;
    public ChatLogListener(EchoLogPlugin plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        var p = e.getPlayer();
        var ctx = new HashMap<String,String>();
        ctx.put("player", p.getName());
        ctx.put("uuid", p.getUniqueId().toString());
        ctx.put("message", e.getMessage());
        ctx.put("world", p.getWorld().getName());
        ctx.put("x", String.valueOf(p.getLocation().getBlockX()));
        ctx.put("y", String.valueOf(p.getLocation().getBlockY()));
        ctx.put("z", String.valueOf(p.getLocation().getBlockZ()));
        plugin.dispatcher().log("logs.chat", ctx);
    }
}
