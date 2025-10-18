package org.Axe1l.echolog.update;

import org.Axe1l.echolog.EchoLogPlugin;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateNotifyListener implements Listener {

    private final EchoLogPlugin plugin;
    private final UpdateChecker checker;

    public UpdateNotifyListener(EchoLogPlugin plugin, UpdateChecker checker) {
        this.plugin = plugin;
        this.checker = checker;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("echolog.update")) return;
        if (!checker.isUpdateAvailable()) return;

        String latest = checker.getLatestVersion();
        String current = plugin.getDescription().getVersion();
        String url = checker.getDownloadUrl();

        // Schöne Chat-Nachricht mit Click
        TextComponent line1 = new TextComponent("§8[§bEcho§3Log§8] §7Update verfügbar: §f"+current+" §7→ §a"+latest);
        TextComponent line2 = new TextComponent("§8[§bEcho§3Log§8] §bKlicken zum Download");
        line2.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        line2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("§7Öffne: §b" + url).create()));

        p.spigot().sendMessage(line1);
        p.spigot().sendMessage(line2);
    }

    /** Für manuelle Ansteuerung per Command */
    public void notifyPlayerNow(Player p) {
        if (!checker.isUpdateAvailable()) {
            p.sendMessage("§8[§bEcho§3Log§8] §7Kein Update verfügbar.");
            return;
        }
        onJoin(new PlayerJoinEvent(p, "")); // reuse
    }
}
