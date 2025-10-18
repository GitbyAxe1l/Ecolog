package org.Axe1l.echolog.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/** Einziger globaler Blocker. Cancelt ganz am Ende. */
public class GuiListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onClick(InventoryClickEvent e) {
        if (!(e.getView().getTopInventory().getHolder() instanceof BaseGUI)) return;

        // Nur Top-Inventar blockieren
        if (e.getClickedInventory() == null || e.getClickedInventory() != e.getView().getTopInventory()) {
            e.setCancelled(true);
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onDrag(InventoryDragEvent e) {
        if (e.getView().getTopInventory().getHolder() instanceof BaseGUI) {
            e.setCancelled(true);
        }
    }
}
