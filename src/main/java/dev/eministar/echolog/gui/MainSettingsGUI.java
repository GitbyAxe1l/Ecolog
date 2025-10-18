package dev.eministar.echolog.gui;

import dev.eministar.echolog.EchoLogPlugin;
import dev.eministar.echolog.util.ItemBuilder;
import dev.eministar.echolog.util.SoundFX;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.Arrays;

public class MainSettingsGUI extends BaseGUI implements Listener {

    private final EchoLogPlugin plugin;

    public MainSettingsGUI(EchoLogPlugin plugin) {
        super(plugin, () -> plugin.lang().t("gui.main_title"),
                plugin.configs().cfg().getInt("gui.rows", 5));
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void render() {
        fillSolid(Material.GRAY_STAINED_GLASS_PANE);

        inv.setItem(10, new ItemBuilder(Material.WRITABLE_BOOK).name("§b"+plugin.lang().t("gui.section_logs"))
                .lore(Arrays.asList("§7"+plugin.lang().t("gui.logs_desc_line1"),
                        "§7"+plugin.lang().t("gui.logs_desc_line2"))).build());

        inv.setItem(12, new ItemBuilder(Material.COMPARATOR).name("§d"+plugin.lang().t("gui.toggles_page"))
                .lore(Arrays.asList(plugin.lang().t("gui.click_toggle"))).build());

        inv.setItem(14, new ItemBuilder(Material.CHEST).name("§b"+plugin.lang().t("gui.outputs_title"))
                .lore(Arrays.asList("§7Discord & "+plugin.lang().t("gui.file_output"))).build());

        inv.setItem(22, new ItemBuilder(Material.NAME_TAG).name("§b"+plugin.lang().t("gui.language"))
                .lore(Arrays.asList("§7"+plugin.lang().t("gui.current")+": §f"+plugin.configs().language(),
                        "§7"+plugin.lang().t("gui.lang_toggle_hint"))).build());

        inv.setItem(size-5, new ItemBuilder(Material.BARRIER).name(plugin.lang().t("gui.close")).build());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onClick(InventoryClickEvent e) {
        // nur unser eigenes Top-Inventar
        if (e.getView().getTopInventory().getHolder() != this) return;

        // immer blocken (keine Itembewegungen)
        e.setCancelled(true);

        // nur Klicks im Top-Inventar verarbeiten
        if (e.getClickedInventory() != e.getView().getTopInventory()) return;

        var p = (Player) e.getWhoClicked();
        var item = e.getCurrentItem();
        if (item == null) return;

        switch (item.getType()) {
            case WRITABLE_BOOK -> { SoundFX.click(p); new LogSettingsGUI(plugin).open(p); }
            case COMPARATOR    -> { SoundFX.click(p); new LogTypeToggleGUI(plugin).open(p); }
            case CHEST         -> { SoundFX.click(p); new OutputSettingsGUI(plugin).open(p); }
            case NAME_TAG      -> {
                SoundFX.click(p);
                String next = plugin.configs().language().equalsIgnoreCase("de") ? "en" : "de";
                plugin.getConfig().set("language", next);
                plugin.saveConfig();
                plugin.lang().reload();
                p.sendMessage(plugin.lang().prefix()+"§7"+plugin.lang().t("messages.lang_set")+" §f"+next);
                new MainSettingsGUI(plugin).open(p);
            }
            case BARRIER       -> { SoundFX.click(p); p.closeInventory(); }
            default -> {}
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onDrag(InventoryDragEvent e) {
        if (e.getView().getTopInventory().getHolder() == this) e.setCancelled(true);
    }
}
