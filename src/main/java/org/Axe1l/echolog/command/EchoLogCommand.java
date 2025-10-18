package org.Axe1l.echolog.command;

import org.Axe1l.echolog.EchoLogPlugin;
import org.Axe1l.echolog.gui.MainSettingsGUI;
import org.Axe1l.echolog.util.SoundFX;
import org.Axe1l.echolog.update.UpdateNotifyListener;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class EchoLogCommand implements CommandExecutor, TabCompleter {

    private final EchoLogPlugin plugin;
    private static final List<String> TYPES = List.of("chat","join_quit","death","commands","teleport","block");

    public EchoLogCommand(EchoLogPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("gui")) {
            if (!(s instanceof Player p)) {
                s.sendMessage(plugin.lang().prefix() + plugin.lang().t("messages.only_player"));
                return true;
            }
            if (!p.hasPermission("echolog.use")) {
                p.sendMessage(plugin.lang().prefix() + plugin.lang().t("messages.no_perm"));
                return true;
            }
            new MainSettingsGUI(plugin).open(p);
            SoundFX.open(p);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!s.hasPermission("echolog.use")) {
                s.sendMessage(plugin.lang().prefix() + plugin.lang().t("messages.no_perm"));
                return true;
            }
            plugin.doReload();
            s.sendMessage(plugin.lang().prefix() + plugin.lang().t("messages.reloaded"));
            return true;
        }

        if (args[0].equalsIgnoreCase("setwebhook")) {
            if (args.length < 3) {
                s.sendMessage("§7Benutzung: §f/echolog setwebhook <type> <url>");
                return true;
            }
            String type = args[1].toLowerCase(Locale.ROOT);
            if (!TYPES.contains(type)) {
                s.sendMessage("§cUnbekannter Typ: §f" + type);
                return true;
            }
            String url = String.join(" ", Arrays.copyOfRange(args, 2, args.length)).trim();
            plugin.getConfig().set("logs." + type + ".discord-webhook", url);
            plugin.saveConfig();
            s.sendMessage(plugin.lang().prefix() + "§aWebhook gesetzt für §f" + type + "§7.");
            return true;
        }

        if (args[0].equalsIgnoreCase("update")) {
            if (!s.hasPermission("echolog.update")) {
                s.sendMessage(plugin.lang().prefix() + plugin.lang().t("messages.no_perm"));
                return true;
            }

            plugin.updates().checkNow(true); // Konsole informieren

            if (s instanceof Player p) {
                if (plugin.updates().isUpdateAvailable()) {
                    new UpdateNotifyListener(plugin, plugin.updates())
                            .notifyPlayerNow(p);
                } else {
                    p.sendMessage(plugin.lang().prefix() + "§7Kein Update verfügbar.");
                }
            } else {
                s.sendMessage(plugin.lang().prefix() + "§7Update-Check ausgeführt.");
            }
            return true;

        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String a, String[] args) {
        if (args.length == 1) return Arrays.asList("gui","reload","setwebhook", "update");
        if (args.length == 2 && "setwebhook".equalsIgnoreCase(args[0])) return TYPES;
        return Collections.emptyList();
    }
}
