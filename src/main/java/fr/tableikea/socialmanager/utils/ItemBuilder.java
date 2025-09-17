package fr.tableikea.socialmanager.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemBuilder {
    private ItemStack item;
    private ItemMeta itemM;

    public ItemBuilder() {}

    public ItemBuilder(Material material, int amount, String displayname, List<String> lore) {
        this.item = new ItemStack(material, amount);
        this.itemM = this.item.getItemMeta();
        this.itemM.setDisplayName(displayname);
        this.itemM.setLore(lore);
        this.item.setItemMeta(this.itemM);
    }

    public ItemBuilder(ItemStack itemStack, int amount, String displayname, List<String> lore) {
        this.item = itemStack;
        this.item.setAmount(amount);
        this.itemM = this.item.getItemMeta();
        this.itemM.setDisplayName(displayname);
        this.itemM.setLore(lore);
        this.item.setItemMeta(this.itemM);
    }

    public ItemBuilder(Material material, int amount, Component displayname, List<Component> lore) {
        this.item = new ItemStack(material, amount);
        this.itemM = this.item.getItemMeta();
        this.itemM.displayName(displayname);
        this.itemM.lore(lore);
        this.item.setItemMeta(this.itemM);
    }

    public ItemBuilder(ItemStack itemStack, int amount, Component displayname, List<Component> lore) {
        this.item = itemStack;
        this.item.setAmount(amount);
        this.itemM = this.item.getItemMeta();
        this.itemM.displayName(displayname);
        this.itemM.lore(lore);
        this.item.setItemMeta(this.itemM);
    }

    public ItemBuilder(Material material, int amount, String displayname) {
        this(material, amount, displayname, null);
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ItemMeta getItemM() {
        return itemM;
    }

    public void setItemM(ItemMeta itemM) {
        this.itemM = itemM;
    }

    public ItemStack toItemStack() {
        return this.item;
    }
}
