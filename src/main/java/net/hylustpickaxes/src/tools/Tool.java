package net.hylustpickaxes.src.tools;

import net.hylustpickaxes.src.upgrades.Upgrade;
import net.hylustpickaxes.src.utils.EnchantNames;
import net.hylustpickaxes.src.utils.MiscUtils;
import net.kyori.adventure.text.Component;

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

    public Tool(ItemStack item, List<Upgrade> upgrades, String tokenName) {
        this.item = item;
        this.upgrades = upgrades;
        this.tokenName = tokenName;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public ItemStack getItem(double multi, int value, double totalSoldPrice, ItemStack previousItem) {
        DecimalFormat df = new DecimalFormat("#,###.00");
        ItemStack copy = item.clone();
        List<Component> lore = new ArrayList<>();
        if (item.getItemMeta().hasLore()) {
	        for (Component line : item.getItemMeta().lore()) {
	            String lineCopy = line.toString().replaceAll("<multiplier>", String.valueOf(multi)).replaceAll("<sold>", df.format(totalSoldPrice)).replaceAll("<value>", String.valueOf(value));
	            lore.add(MiscUtils.chat(lineCopy));
	        }
        }

        ItemMeta meta = copy.getItemMeta();
        meta.lore(lore);
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
