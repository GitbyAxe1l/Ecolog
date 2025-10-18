package org.Axe1l.echolog.config;

import org.Axe1l.echolog.EchoLogPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class ConfigManager {
    private final EchoLogPlugin plugin;

    public ConfigManager(EchoLogPlugin plugin) {
        this.plugin = plugin;
        ensure();
    }

    private void ensure() {
        plugin.saveDefaultConfig();
        // ensure lang folder
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists()) langDir.mkdirs();

        saveIfMissing("lang/lang_de.yml");
        saveIfMissing("lang/lang_en.yml");
    }

    private void saveIfMissing(String name) {
        File f = new File(plugin.getDataFolder(), name);
        if (!f.exists()) plugin.saveResource(name, false);
    }

    public void reload() {
        plugin.reloadConfig();
    }

    public FileConfiguration cfg() {
        return plugin.getConfig();
    }

    public String language() {
        return cfg().getString("language", "de").toLowerCase();
    }
}
