package net.hylustpickaxes.src.tools;

import net.hylustpickaxes.src.nbt.NBT;
import net.hylustpickaxes.src.nbt.NBTList;
import net.hylustpickaxes.src.upgrades.Upgrade;
import net.hylustpickaxes.src.utils.EnchantNames;
import net.hylustpickaxes.src.utils.MiscUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        List<String> lore = new ArrayList<>();
        for (String line : item.getItemMeta().getLore()) {
            String lineCopy = line.replaceAll("<multiplier>", String.valueOf(multi)).replaceAll("<sold>", df.format(totalSoldPrice)).replaceAll("<value>", String.valueOf(value));
            lore.add(MiscUtils.chat(lineCopy));
        }

        ItemMeta meta = copy.getItemMeta();
        meta.setLore(lore);
        copy.setItemMeta(meta);
        NBT toolNBT = NBT.get(copy);
        toolNBT.setInt("value", value);
        toolNBT.setString("totalSold", String.valueOf(totalSoldPrice));
        toolNBT.setString("multiplier", String.valueOf(multi));
        NBTList ench = new NBTList();
        toolNBT.set("ench", ench);
        if (previousItem != null) {
            NBT mainNBT = NBT.get(previousItem);
            if (upgrades != null && !upgrades.isEmpty()) {
                for (Upgrade upgrade : upgrades) {
                    toolNBT.setInt("upgrade." + upgrade.getName(), mainNBT.getInt("upgrade." + upgrade.getName()));

                }
                copy = toolNBT.apply(copy);

                for (Upgrade upgrade : upgrades) {
                    String[] line = upgrade.getType().split(":");
                    if (line.length > 1 && line[0].equalsIgnoreCase("enchant")) {
                        if (EnchantNames.getEnchantment(line[1]) != null) {
                            copy.addUnsafeEnchantment(EnchantNames.getEnchantment(line[1]), mainNBT.getInt("upgrade." + upgrade.getName()));
                        }
                    }
                }
            }

        } else {
            copy = toolNBT.apply(copy);
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
