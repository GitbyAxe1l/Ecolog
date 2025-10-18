package dev.eministar.echolog.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemBuilder {
    private final ItemStack stack;

    public ItemBuilder(Material mat) { this.stack = new ItemStack(mat); }
    public ItemBuilder amount(int amount) { stack.setAmount(amount); return this; }
    public ItemBuilder name(String name) {
        ItemMeta m = stack.getItemMeta();
        m.setDisplayName(name);
        stack.setItemMeta(m);
        return this;
    }
    public ItemBuilder lore(List<String> lore) {
        ItemMeta m = stack.getItemMeta();
        m.setLore(lore);
        stack.setItemMeta(m);
        return this;
    }
    public ItemBuilder model(int data) {
        ItemMeta m = stack.getItemMeta();
        try { m.setCustomModelData(data); } catch (Throwable ignored) {}
        stack.setItemMeta(m);
        return this;
    }
    public ItemBuilder hideAll() {
        ItemMeta m = stack.getItemMeta();
        m.addItemFlags(ItemFlag.values());
        stack.setItemMeta(m);
        return this;
    }

    public ItemBuilder enchant(org.bukkit.enchantments.Enchantment ench, int level) {
        var m = stack.getItemMeta();
        m.addEnchant(ench, level, true);
        stack.setItemMeta(m);
        return this;
    }

    public ItemBuilder flags(org.bukkit.inventory.ItemFlag... flags) {
        var m = stack.getItemMeta();
        m.addItemFlags(flags);
        stack.setItemMeta(m);
        return this;
    }
    public ItemStack build() { return stack; }
}
