package dev.eministar.echolog.util;

import org.bukkit.ChatColor;

public class MessageUtil {
    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
