package dev.eministar.echolog.gui;

import dev.eministar.echolog.EchoLogPlugin;
import dev.eministar.echolog.util.ItemBuilder;
import dev.eministar.echolog.util.SoundFX;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.net.URI;
import java.util.Arrays;

public class OutputSettingsGUI extends BaseGUI implements Listener {

    private final NamespacedKey KEY_PATH;

    public OutputSettingsGUI(EchoLogPlugin plugin) {
        super(plugin, () -> "§f🣤 §dEchoLog §8› §7" + plugin.lang().t("gui.outputs_title"), 5);
        this.KEY_PATH = new NamespacedKey(plugin, "echolog_path");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void render() {
        var c = plugin.getConfig();
        fillSolid(Material.GRAY_STAINED_GLASS_PANE);

        String status = plugin.lang().t("gui.status");
        String on  = plugin.lang().t("gui.status_on");
        String off = plugin.lang().t("gui.status_off");

        boolean file = c.getBoolean("outputs.file.enabled", true);
        boolean disc = c.getBoolean("outputs.discord.enabled", false);

        // Toggles (PDC mit Pfad!)
        inv.setItem(10, buildToggleItem(
                "outputs.file.enabled",
                file,
                (file ? "§a" : "§c") + plugin.lang().t("gui.file_output"),
                Arrays.asList(
                        "§7" + status + ": " + (file ? on : off),
                        "§7" + plugin.lang().t("gui.click_toggle")
                )
        ));

        inv.setItem(12, buildToggleItem(
                "outputs.discord.enabled",
                disc,
                (disc ? "§a" : "§c") + plugin.lang().t("gui.discord_output"),
                Arrays.asList(
                        "§7" + status + ": " + (disc ? on : off),
                        "§7" + plugin.lang().t("gui.click_toggle")
                )
        ));

        // Folder (File)
        inv.setItem(21, new ItemBuilder(Material.BOOK)
                .name("§b" + plugin.lang().t("gui.folder_label"))
                .lore(Arrays.asList(
                        "§7" + plugin.lang().t("gui.current") + ": §f" + c.getString("outputs.file.folder", "logs/echolog"),
                        "§7" + plugin.lang().t("gui.folder_hint")
                )).build());

        // Default Webhook (Discord)
        String curWebhook = c.getString("outputs.discord.default-webhook", "");
        inv.setItem(23, new ItemBuilder(Material.CHAIN)
                .name("§b" + plugin.lang().t("gui.default_webhook"))
                .lore(Arrays.asList(
                        "§7" + plugin.lang().t("gui.current") + ": " +
                                (curWebhook == null || curWebhook.isBlank()
                                        ? "§8(" + plugin.lang().t("gui.none") + ")"
                                        : "§f" + shorten(curWebhook)),
                        "§7" + plugin.lang().t("gui.webhook_hint")
                )).build());

        // Navigation
        inv.setItem(36, new ItemBuilder(Material.ARROW).name("§7" + plugin.lang().t("gui.prev")).build());
        inv.setItem(40, new ItemBuilder(Material.BARRIER).name("§c" + plugin.lang().t("gui.close")).build());
    }

    // == Event-Handling ==

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onClick(InventoryClickEvent e) {
        var top = e.getView().getTopInventory();
        if (top == null || top.getHolder() != this) return;

        // Blocke jegliche Itembewegung
        e.setCancelled(true);

        int raw = e.getRawSlot();
        if (raw < 0 || raw >= top.getSize()) return;
        if (!(e.getWhoClicked() instanceof Player p)) return;

        var it = top.getItem(raw);
        if (it == null || it.getType() == Material.AIR) return;

        var c = plugin.getConfig();

        switch (it.getType()) {
            case ARROW -> {
                SoundFX.click(p);
                new MainSettingsGUI(plugin).open(p);
            }
            case BARRIER -> {
                SoundFX.click(p);
                p.closeInventory();
            }
            case LIME_CONCRETE, RED_CONCRETE -> {
                // Toggle via PDC (nie mehr Lore-gebunden)
                var meta = it.getItemMeta();
                if (meta == null) return;

                String path = meta.getPersistentDataContainer().get(KEY_PATH, PersistentDataType.STRING);
                if (path == null || path.isBlank()) return;

                boolean cur = c.getBoolean(path, false);
                c.set(path, !cur);
                plugin.saveConfig();
                plugin.reloadConfig(); // sicherstellen, dass render() frische Werte sieht

                if (!cur) SoundFX.on(p); else SoundFX.off(p);

                render();
                p.updateInventory();
            }
            case BOOK -> {
                SoundFX.click(p);
                p.sendMessage(plugin.lang().prefix() + "§7" + plugin.lang().t("gui.enter_folder"));
                p.sendMessage("§7" + plugin.lang().t("gui.cancel_tip"));

                Bukkit.getPluginManager().registerEvents(new Listener() {
                    @EventHandler
                    public void onChat(AsyncPlayerChatEvent ev) {
                        if (!ev.getPlayer().equals(p)) return;
                        ev.setCancelled(true);
                        org.bukkit.event.HandlerList.unregisterAll(this);
                        String msg = ev.getMessage().trim();

                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            if (msg.equalsIgnoreCase("cancel") || msg.equalsIgnoreCase("abbrechen")) {
                                SoundFX.err(p);
                                p.sendMessage(plugin.lang().prefix() + "§7" + plugin.lang().t("gui.cancelled"));
                            } else {
                                c.set("outputs.file.folder", msg);
                                plugin.saveConfig();
                                plugin.reloadConfig();
                                SoundFX.ok(p);
                                p.sendMessage(plugin.lang().prefix() + "§a" + plugin.lang().t("gui.updated"));
                            }
                            render();
                            p.updateInventory();
                            p.openInventory(getInventory());
                        });
                    }
                }, plugin);

                p.closeInventory();
            }
            case CHAIN -> {
                SoundFX.click(p);
                p.sendMessage(plugin.lang().prefix() + "§7" + plugin.lang().t("gui.enter_webhook"));
                p.sendMessage("§7" + plugin.lang().t("gui.cancel_tip"));

                Bukkit.getPluginManager().registerEvents(new Listener() {
                    @EventHandler
                    public void onChat(AsyncPlayerChatEvent ev) {
                        if (!ev.getPlayer().equals(p)) return;
                        ev.setCancelled(true);
                        org.bukkit.event.HandlerList.unregisterAll(this);
                        String msg = ev.getMessage().trim();

                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            if (msg.equalsIgnoreCase("cancel") || msg.equalsIgnoreCase("abbrechen")) {
                                SoundFX.err(p);
                                p.sendMessage(plugin.lang().prefix() + "§7" + plugin.lang().t("gui.cancelled"));
                            } else if (!looksLikeHttpsUrl(msg)) {
                                SoundFX.err(p);
                                p.sendMessage(plugin.lang().prefix() + "§c" + plugin.lang().t("gui.invalid_url"));
                            } else {
                                c.set("outputs.discord.default-webhook", msg);
                                plugin.saveConfig();
                                plugin.reloadConfig();
                                SoundFX.ok(p);
                                p.sendMessage(plugin.lang().prefix() + "§a" + plugin.lang().t("gui.updated"));
                            }
                            render();
                            p.updateInventory();
                            p.openInventory(getInventory());
                        });
                    }
                }, plugin);

                p.closeInventory();
            }
            default -> { /* no-op */ }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onDrag(InventoryDragEvent e) {
        var top = e.getView().getTopInventory();
        if (top == null || top.getHolder() != this) return;
        for (int slot : e.getRawSlots()) {
            if (slot >= 0 && slot < top.getSize()) {
                e.setCancelled(true);
                return;
            }
        }
    }

    // == Helpers ==

    private String shorten(String s) {
        return (s != null && s.length() > 40) ? s.substring(0, 37) + "..." : (s == null ? "" : s);
    }

    private boolean looksLikeHttpsUrl(String url) {
        try {
            if (!url.startsWith("http")) return false;
            URI u = URI.create(url);
            return "https".equalsIgnoreCase(u.getScheme()) && u.getHost() != null && url.contains("/");
        } catch (Exception e) {
            return false;
        }
    }

    private org.bukkit.inventory.ItemStack buildToggleItem(String path, boolean enabled, String name, java.util.List<String> lore) {
        var item = new ItemBuilder(enabled ? Material.LIME_CONCRETE : Material.RED_CONCRETE)
                .name(name)
                .lore(lore)
                .build();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(KEY_PATH, PersistentDataType.STRING, path);
            item.setItemMeta(meta);
        }
        return item;
    }
}
