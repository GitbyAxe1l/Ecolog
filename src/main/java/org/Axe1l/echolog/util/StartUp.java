package org.Axe1l.echolog.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;

public final class StartUp {

    private StartUp() {}

    public static void printBanner(String version) {
        String[] logo = {
                "",
                "  ______     _           _                 ",
                " |  ____|   | |         | |                ",
                " | |__   ___| |__   ___ | |     ___   __ _ ",
                " |  __| / __| '_ \\ / _ \\| |    / _ \\ / _` |",
                " | |___| (__| | | | (_) | |___| (_) | (_| |",
                " |______\\___|_| |_|\\___/|______\\___/ \\__, |",
                "                                      __/ |",
                "                                     |___/ ",
                ""
        };

        String sep = "§8───────────────────────────────────────────────";

        for (String line : logo) {
            Bukkit.getConsoleSender().sendMessage("§b" + line);
        }

        Bukkit.getConsoleSender().sendMessage(sep);
        Bukkit.getConsoleSender().sendMessage(" §fPlugin : §bEcho§3Log");
        Bukkit.getConsoleSender().sendMessage(" §fVersion: §a" + version);
        Bukkit.getConsoleSender().sendMessage(" §fAuthor : §eEmin (Eministar)");
        Bukkit.getConsoleSender().sendMessage(" §fSupport: §7https://discord.gg/ErFRp9eSrj");
        Bukkit.getConsoleSender().sendMessage(sep);
    }

    /** Convenience, falls du keinen Version-String übergibst */
    public static void printBanner() {
        PluginDescriptionFile desc = Bukkit.getPluginManager().getPlugin("EchoLog").getDescription();
        printBanner(desc.getVersion());
    }
}
