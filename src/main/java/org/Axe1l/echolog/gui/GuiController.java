package org.Axe1l.echolog.gui;

import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

public class GuiController implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof BaseGUI)) return;

        // nur Top-Inventar behandeln
        if (e.getClickedInventory() == null || e.getRawSlot() >= e.getInventory().getSize()) {
            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);

        if (e.getWhoClicked() instanceof org.bukkit.entity.Player p) {
            try {
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.6f, 1.05f);
            } catch (Throwable ignored) {}
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof BaseGUI) e.setCancelled(true);
    }
}
