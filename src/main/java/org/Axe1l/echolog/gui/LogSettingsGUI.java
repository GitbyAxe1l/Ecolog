package org.Axe1l.echolog.gui;

import org.Axe1l.echolog.EchoLogPlugin;
import org.Axe1l.echolog.util.ItemBuilder;
import org.Axe1l.echolog.util.SoundFX;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.function.Consumer;

public class LogSettingsGUI extends BaseGUI implements Listener {

    private String basePath = "logs.chat";

    public LogSettingsGUI(EchoLogPlugin plugin) {
        // BaseGUI sollte: this als Holder nutzen
        super(plugin, () -> "§dEchoLog §8› §7" + plugin.lang().t("gui.section_logs"), 5);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void render() {
        fillSolid(Material.GRAY_STAINED_GLASS_PANE);

        // Kategorien
        set(10, Material.PAPER,          "§f"+plugin.lang().t("gui.item_chat"),      "§8logs.chat");
        set(11, Material.PLAYER_HEAD,    "§f"+plugin.lang().t("gui.item_joinquit"),  "§8logs.join_quit");
        set(12, Material.SKELETON_SKULL, "§f"+plugin.lang().t("gui.item_death"),     "§8logs.death");
        set(13, Material.COMMAND_BLOCK,  "§f"+plugin.lang().t("gui.item_commands"),  "§8logs.commands");
        set(14, Material.ENDER_PEARL,    "§f"+plugin.lang().t("gui.item_teleport"),  "§8logs.teleport");
        set(15, Material.IRON_PICKAXE,   "§f"+plugin.lang().t("gui.item_block"),     "§8logs.block");

        drawCurrent();

        inv.setItem(36, new ItemBuilder(Material.ARROW).name("§7"+plugin.lang().t("gui.prev")).build());
        inv.setItem(40, new ItemBuilder(Material.BARRIER).name("§c"+plugin.lang().t("gui.close")).build());
    }

    private void set(int slot, Material mat, String name, String lore) {
        inv.setItem(slot, new ItemBuilder(mat).name(name).lore(List.of(lore)).build());
    }

    private void drawCurrent() {
        FileConfiguration c = plugin.getConfig();
        boolean enabled    = c.getBoolean(basePath+".enabled", true);
        boolean simplified = c.getBoolean(basePath+".simplified-message", false);
        String  fmtPath    = formatPath(basePath);
        String  fmt        = c.getString(fmtPath, defaultFormat(basePath));

        String statusLabel = plugin.lang().t("gui.status");
        String onTxt  = plugin.lang().t("gui.status_on");
        String offTxt = plugin.lang().t("gui.status_off");

        // Enabled Toggle
        inv.setItem(28, new ItemBuilder(enabled?Material.LIME_CONCRETE:Material.RED_CONCRETE)
                .name((enabled?"§a":"§c") + plugin.lang().t("gui.enabled_toggle"))
                .lore(List.of(
                        "§7"+statusLabel+": " + (enabled?onTxt:offTxt),
                        "§7"+plugin.lang().t("gui.click_toggle"),
                        "§8"+basePath+".enabled"
                )).build());

        // Simplified Toggle
        inv.setItem(29, new ItemBuilder(simplified?Material.LIME_DYE:Material.RED_DYE)
                .name((simplified?"§a":"§c") + plugin.lang().t("gui.simplified_label"))
                .lore(List.of(
                        "§7"+statusLabel+": " + (simplified?onTxt:offTxt),
                        "§7"+plugin.lang().t("gui.simplified_desc"),
                        "§8"+basePath+".simplified-message"
                )).build());

        // Format Editor
        inv.setItem(33, new ItemBuilder(Material.NAME_TAG)
                .name("§b"+plugin.lang().t("gui.format_edit"))
                .lore(List.of(
                        "§7"+plugin.lang().t("gui.current")+":",
                        "§f"+fmt,
                        "§7"+plugin.lang().t("gui.format_hint"),
                        "§8"+fmtPath
                )).build());

        // Test Button
        inv.setItem(34, new ItemBuilder(Material.NOTE_BLOCK)
                .name("§d"+plugin.lang().t("gui.test_send"))
                .lore(List.of("§7"+plugin.lang().t("gui.test_send_desc"))).build());
    }

    private String formatPath(String base) {
        return switch (base) {
            case "logs.chat"      -> "logs.chat.format";
            case "logs.join_quit" -> "logs.join_quit.join-format";
            case "logs.death"     -> "logs.death.format";
            case "logs.commands"  -> "logs.commands.format";
            case "logs.teleport"  -> "logs.teleport.format";
            case "logs.block"     -> "logs.block.format";
            default -> base + ".format";
        };
    }

    private String defaultFormat(String base) {
        return switch (base) {
            case "logs.chat"      -> "[%time%] %player%: %message%";
            case "logs.join_quit" -> "[%time%] + %player% joined (%uuid%)";
            case "logs.death"     -> "[%time%] ☠ %player% died: %reason% at %world% (%x%/%y%/%z%)";
            case "logs.commands"  -> "[%time%] /%command% by %player%";
            case "logs.teleport"  -> "[%time%] %player% teleported %from% -> %to%";
            case "logs.block"     -> "[%time%] %player% %action% %block% at %world% (%x%/%y%/%z%)";
            default -> "[%time%] %message%";
        };
    }

    // ===================== EVENT-HANDLER =====================

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onClick(InventoryClickEvent e) {
        try {
            Inventory top = e.getView().getTopInventory();
            // prüfe exakt unser GUI
            if (!(top == getInventory() || top.getHolder() == this)) return;

            int topSize = top.getSize();
            int raw = e.getRawSlot();
            boolean inTop = raw >= 0 && raw < topSize;

            // immer blocken
            e.setCancelled(true);
            if (!inTop) return;

            if (!(e.getWhoClicked() instanceof Player p)) return;

            var it = e.getCurrentItem();
            if (it == null || it.getType() == Material.AIR) return;
            if (it.getItemMeta() == null) return;

            switch (it.getType()) {
                case ARROW -> { SoundFX.click(p); new MainSettingsGUI(plugin).open(p); }
                case BARRIER -> { SoundFX.click(p); p.closeInventory(); }

                // Kategorie-Wechsel
                case PAPER, PLAYER_HEAD, SKELETON_SKULL, COMMAND_BLOCK, ENDER_PEARL, IRON_PICKAXE -> {
                    var lore = it.getItemMeta().getLore();
                    if (lore == null || lore.isEmpty()) return;
                    String tag = lore.get(0); // Java 17 safe
                    if (tag != null && tag.startsWith("§8")) {
                        basePath = tag.substring(2); // e.g. logs.chat
                        SoundFX.click(p);
                        render();
                        p.updateInventory();
                    }
                }

                // Toggles schreiben direkt in config.yml
                case LIME_CONCRETE, RED_CONCRETE, LIME_DYE, RED_DYE -> {
                    var lore = it.getItemMeta().getLore();
                    if (lore == null || lore.isEmpty()) return;
                    String path = lore.get(lore.size()-1);
                    if (path == null || !path.startsWith("§8")) return;
                    path = path.substring(2); // z.B. logs.chat.enabled

                    boolean cur = plugin.getConfig().getBoolean(path, false);
                    plugin.getConfig().set(path, !cur);
                    plugin.saveConfig(); // <-- sofort in config.yml persistieren

                    if (!cur) SoundFX.on(p); else SoundFX.off(p);

                    render();
                    p.updateInventory();
                }

                // Format bearbeiten (Chat-Eingabe)
                case NAME_TAG -> {
                    SoundFX.click(p);
                    String path = formatPath(basePath);
                    p.sendMessage(plugin.lang().prefix()+"§7"+plugin.lang().t("gui.format_editing")+" §8"+path);
                    p.sendMessage("§8"+plugin.lang().t("gui.format_placeholders"));
                    p.sendMessage("§7→ "+plugin.lang().t("gui.format_send_cancel"));
                    askInChatOnce(p, null, input -> {
                        if (input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("abbrechen")) {
                            SoundFX.err(p);
                            p.sendMessage(plugin.lang().prefix()+"§7"+plugin.lang().t("gui.cancelled"));
                            return;
                        }
                        plugin.getConfig().set(path, input);
                        plugin.saveConfig(); // <-- direkt speichern
                        SoundFX.ok(p);
                        p.sendMessage(plugin.lang().prefix()+"§a"+plugin.lang().t("gui.format_saved"));
                    });
                    p.closeInventory();
                }

                // Test-Dispatch
                case NOTE_BLOCK -> {
                    SoundFX.click(p);
                    plugin.dispatcher().testFor(basePath, p);
                    p.sendMessage(plugin.lang().prefix()+"§7"+plugin.lang().t("gui.test_sent_for")+" §f"+basePath+"§7.");
                }

                default -> {}
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onDrag(InventoryDragEvent e) {
        Inventory top = e.getView().getTopInventory();
        if (!(top == getInventory() || top.getHolder() == this)) return;
        e.setCancelled(true);
    }

    // ===================== Helpers =====================

    private void askInChatOnce(Player p, String prompt, Consumer<String> onAnswer) {
        if (prompt != null && !prompt.isBlank()) {
            p.sendMessage(plugin.lang().prefix()+"§7"+prompt);
            p.sendMessage("§7"+plugin.lang().t("gui.cancel_tip"));
        }
        Listener l = new Listener() {
            @EventHandler
            public void onChat(AsyncPlayerChatEvent ev) {
                if (!ev.getPlayer().equals(p)) return;
                ev.setCancelled(true);
                HandlerList.unregisterAll(this);
                String msg = ev.getMessage().trim();
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    try {
                        onAnswer.accept(msg);
                    } finally {
                        render();
                        p.openInventory(getInventory());
                        p.updateInventory();
                    }
                });
            }
        };
        Bukkit.getPluginManager().registerEvents(l, plugin);
    }
}
