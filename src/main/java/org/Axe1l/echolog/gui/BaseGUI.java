package org.Axe1l.echolog.gui;

import org.Axe1l.echolog.EchoLogPlugin;
import org.Axe1l.echolog.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.function.Supplier;

public abstract class BaseGUI implements InventoryHolder {
    protected final EchoLogPlugin plugin;
    private final Supplier<String> titleSupplier;
    protected final int size;
    protected Inventory inv;

    protected BaseGUI(EchoLogPlugin plugin, Supplier<String> titleSupplier, int rows) {
        this.plugin = plugin;
        this.titleSupplier = titleSupplier;
        this.size = Math.max(1, Math.min(6, rows)) * 9;
        this.inv = Bukkit.createInventory(this, this.size, safeTitle());
    }

    /** Subklassen zeichnen hier ihre Items auf 'inv'. */
    public abstract void render();

    /** Öffnet IMMER mit frisch geladener Config & aktuellem Titel. */
    public void open(Player p) {
        // 1) Datei → Memory
        plugin.reloadConfig();
        // 2) neues Inventar mit aktuellem Titel
        this.inv = Bukkit.createInventory(this, this.size, safeTitle());
        // 3) mit aktuellen Werten rendern
        render();
        p.openInventory(inv);
    }

    protected void fillSolid(Material mat) {
        var blank = new ItemBuilder(mat).name(" ").build();
        for (int i = 0; i < size; i++) inv.setItem(i, blank);
    }

    @Override public Inventory getInventory() { return inv; }

    private String safeTitle() {
        try { return titleSupplier.get(); }
        catch (Exception e) { return "EchoLog"; }
    }
}
