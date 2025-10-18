package dev.eministar.echolog.gui;

import dev.eministar.echolog.EchoLogPlugin;
import dev.eministar.echolog.util.ItemBuilder;
import dev.eministar.echolog.util.SoundFX;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class LogTypeToggleGUI extends BaseGUI implements Listener {

    private final EchoLogPlugin plugin;

    private final Map<Integer, String> PATHS = new LinkedHashMap<>() {{
        put(10, "logs.chat.enabled");
        put(12, "logs.join_quit.enabled");
        put(14, "logs.death.enabled");
        put(28, "logs.commands.enabled");
        put(30, "logs.teleport.enabled");
        put(32, "logs.block.enabled");
    }};

    public LogTypeToggleGUI(EchoLogPlugin plugin) {
        super(plugin, () -> "§dEchoLog §8› §7" + plugin.lang().t("gui.toggles_page"), 5);
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override public void render() {
        fillSolid(Material.GRAY_STAINED_GLASS_PANE);

        setToggle(10, "logs.chat.enabled",       plugin.lang().t("gui.item_chat"));
        setToggle(12, "logs.join_quit.enabled",  plugin.lang().t("gui.item_joinquit"));
        setToggle(14, "logs.death.enabled",      plugin.lang().t("gui.item_death"));
        setToggle(28, "logs.commands.enabled",   plugin.lang().t("gui.item_commands"));
        setToggle(30, "logs.teleport.enabled",   plugin.lang().t("gui.item_teleport"));
        setToggle(32, "logs.block.enabled",      plugin.lang().t("gui.item_block"));

        inv.setItem(36, new ItemBuilder(Material.ARROW).name(plugin.lang().t("gui.prev")).build());
        inv.setItem(40, new ItemBuilder(Material.BARRIER).name(plugin.lang().t("gui.close")).build());
    }

    private void setToggle(int slot, String path, String label) {
        FileConfiguration c = plugin.getConfig();
        boolean enabled = c.getBoolean(path, true);
        String status = enabled ? plugin.lang().t("gui.status_on") : plugin.lang().t("gui.status_off");

        var ib = new ItemBuilder(enabled ? Material.LIME_DYE : Material.RED_DYE)
                .name((enabled ? "§a" : "§c") + label)
                .lore(Arrays.asList("§7" + plugin.lang().t("gui.status") + ": " + status,
                        "§7" + plugin.lang().t("gui.click_toggle"),
                        "§8" + path));
        if (enabled) ib.enchant(Enchantment.DENSITY, 1).flags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        inv.setItem(slot, ib.build());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTopInventory().getHolder() != this) return;
        e.setCancelled(true);
        if (e.getClickedInventory() != e.getView().getTopInventory()) return;

        var p = (Player) e.getWhoClicked();
        var item = e.getCurrentItem();
        if (item == null || item.getItemMeta() == null) return;

        switch (item.getType()) {
            case BARRIER -> { SoundFX.click(p); p.closeInventory(); }
            case ARROW   -> { SoundFX.click(p); new MainSettingsGUI(plugin).open(p); }
            case LIME_DYE, RED_DYE -> {
                String path = PATHS.get(e.getRawSlot());
                if (path == null) return;
                boolean cur = plugin.getConfig().getBoolean(path, true);
                plugin.getConfig().set(path, !cur);
                plugin.saveConfig();
                if (!cur) SoundFX.on(p); else SoundFX.off(p);
                render(); p.updateInventory();
            }
            default -> {}
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onDrag(InventoryDragEvent e) {
        if (e.getView().getTopInventory().getHolder() == this) e.setCancelled(true);
    }
}
