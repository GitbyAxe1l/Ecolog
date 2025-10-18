package org.Axe1l.echolog.config;

import org.Axe1l.echolog.EchoLogPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LangManager {
    private final EchoLogPlugin plugin;
    private FileConfiguration lang;

    public LangManager(EchoLogPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        String code = plugin.configs().language();
        File file = new File(plugin.getDataFolder(), "lang/lang_" + code + ".yml");
        if (!file.exists()) {
            plugin.saveResource("lang/lang_" + code + ".yml", false);
        }
        this.lang = YamlConfiguration.loadConfiguration(file);
    }

    public String t(String path) {
        return lang.getString(path, path);
    }

    public String prefix() {
        return t("prefix");
    }
}
