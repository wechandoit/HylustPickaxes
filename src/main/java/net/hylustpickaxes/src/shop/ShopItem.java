package net.hylustpickaxes.src.shop;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopItem {

    private ItemStack item;
    private int price;
    private String type;
    private int slot;
    private List<String> commands;
    private int randMin;
    private int randMax;
    private Enchantment enchantment;

    public ShopItem(ItemStack i, int p, int s) {
        item = i;
        price = p;
        type = "ITEM";
        slot = s;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public void setEnchantment(Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    public int getRandMin() {
        return randMin;
    }

    public void setRandMin(int randMin) {
        this.randMin = randMin;
    }

    public int getRandMax() {
        return randMax;
    }

    public void setRandMax(int randMax) {
        this.randMax = randMax;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int s) {
        slot = s;
    }

}
