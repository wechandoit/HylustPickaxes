package net.hylustpickaxes.src.tools;

import net.hylustpickaxes.src.Main;
import net.hylustpickaxes.src.upgrades.Upgrade;
import net.hylustpickaxes.src.utils.EnchantNames;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.nbtapi.NBT;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Tool {
    private ItemStack item;
    private List<Upgrade> upgrades;
    private String tokenName;
    private List<String> lore;

    public Tool(ItemStack item, List<Upgrade> upgrades, String tokenName, List<String> lore) {
        this.item = item;
        this.upgrades = upgrades;
        this.tokenName = tokenName;
        this.lore = lore;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }
    
    public List<String> getLore() {
    	return lore;
    }

    public ItemStack getItem(double multi, int value, double totalSoldPrice, ItemStack previousItem) {
        ItemStack copy = item.clone();
        List<Component> lore = new ArrayList<>();
        if (this.lore != null && !this.lore.isEmpty()) {
	        for (String line : this.lore) {
	            lore.add(Main.mm.deserialize(line, Placeholder.unparsed("value", String.valueOf(value))).decoration(TextDecoration.ITALIC, false));
	        }
        }

        ItemMeta meta = copy.getItemMeta();
        meta.lore(lore);
        meta.setUnbreakable(true);
        copy.setItemMeta(meta);
        NBT.modify(copy, toolNBT -> {
	        toolNBT.setInteger("value", value);
	        toolNBT.setString("totalSold", String.valueOf(totalSoldPrice));
	        toolNBT.setString("multiplier", String.valueOf(multi));
	        
	        if (previousItem != null) {
	        	for (Upgrade upgrade : upgrades) {
	        		toolNBT.setInteger("upgrade." + upgrade.getName(), (int) NBT.get(previousItem, mainNBT -> (int) mainNBT.getInteger("upgrade." + upgrade.getName())));
	        	}
	        }
        });
        
        if (previousItem != null) {
            if (upgrades != null && !upgrades.isEmpty()) {
                for (Upgrade upgrade : upgrades) {
                    String[] line = upgrade.getType().split(":");
                    if (line.length > 1 && line[0].equalsIgnoreCase("enchant")) {
                        if (EnchantNames.getEnchantment(line[1]) != null) {
                            copy.addUnsafeEnchantment(EnchantNames.getEnchantment(line[1]), (int) NBT.get(previousItem, mainNBT -> (int) mainNBT.getInteger("upgrade." + upgrade.getName())));
                        }
                    }
                }
            }

        }
        return copy;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public List<Upgrade> getUpgrades() {
        return upgrades;
    }

    public void setUpgrades(List<Upgrade> upgrades) {
        this.upgrades = upgrades;
    }

    public String toString() {
        return "ItemStack: " + item + "\n" + "upgrades: " + upgrades + "\n" + "tokenName: " + tokenName;
    }
}
