package dev.eministar.echolog;

import dev.eministar.echolog.command.EchoLogCommand;
import dev.eministar.echolog.config.ConfigManager;
import dev.eministar.echolog.config.LangManager;
import dev.eministar.echolog.gui.GuiListener;
import dev.eministar.echolog.input.ChatInputManager;
import dev.eministar.echolog.listeners.*;
import dev.eministar.echolog.logging.LogDispatcher;
import dev.eministar.echolog.util.StartUp;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import dev.eministar.echolog.update.UpdateChecker;
import dev.eministar.echolog.update.UpdateNotifyListener;

public class EchoLogPlugin extends JavaPlugin {

    private static EchoLogPlugin instance;
    private ConfigManager configManager;
    private LangManager langManager;
    private LogDispatcher logDispatcher;
    private ChatInputManager chatInputManager;
    private UpdateChecker updateChecker;
    public UpdateChecker updates() { return updateChecker; }

    public static EchoLogPlugin get() { return instance; }
    public ConfigManager configs() { return configManager; }
    public LangManager lang() { return langManager; }
    public LogDispatcher dispatcher() { return logDispatcher; }
    public ChatInputManager chatInput() { return chatInputManager; }

    @Override
    public void onEnable() {
        StartUp.printBanner();
        instance = this;

        saveDefaultConfig();
        this.configManager = new ConfigManager(this);
        this.langManager = new LangManager(this);
        this.logDispatcher = new LogDispatcher(this);
        this.chatInputManager = new ChatInputManager(this);

        this.updateChecker = new UpdateChecker(this);
        this.updateChecker.startAsyncCheck();
        new UpdateNotifyListener(this, this.updateChecker);

        // Commands
        EchoLogCommand cmd = new EchoLogCommand(this);
        getCommand("echolog").setExecutor(cmd);
        getCommand("echolog").setTabCompleter(cmd);

        Bukkit.getPluginManager().registerEvents(new dev.eministar.echolog.gui.GuiListener(), this);

        // Events
        Bukkit.getPluginManager().registerEvents(new ChatLogListener(this), this);
        Bukkit.getPluginManager().registerEvents(new JoinQuitLogListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DeathLogListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CommandLogListener(this), this);
        Bukkit.getPluginManager().registerEvents(new TeleportLogListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockLogListener(this), this);

        getLogger().info("EchoLog v2 aktiviert.");
    }

    @Override
    public void onDisable() {
        getLogger().info("EchoLog deaktiviert.");
    }

    /** Konsistentes Reload (Config + Lang + Dispatcher) */
    public void doReload() {
        reloadConfig();
        configManager.reload();
        langManager.reload();
        logDispatcher.reload();
        //hello
    }
}
