package net.hylustpickaxes.src.gui;

import net.hylustpickaxes.src.Main;
import net.hylustpickaxes.src.config.ConfigData;
import net.hylustpickaxes.src.shop.ShopItem;
import net.hylustpickaxes.src.upgrades.Upgrade;
import net.hylustpickaxes.src.utils.MiscUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.nbtapi.NBT;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GUIManager {

    private static File file = new File("plugins/HylustPickaxes/", "config.yml");
    private static FileConfiguration data = YamlConfiguration.loadConfiguration(file);

    public static Inventory getUpgradeMenu(List<Upgrade> upgradeList, Player player) {
        Inventory inv = Bukkit.createInventory(null, ConfigData.rows * 9, MiscUtils.chat(ConfigData.upgradeMenuName));
        DecimalFormat df = new DecimalFormat("#,##0.00");
        ItemStack mainHand = player.getInventory().getItemInMainHand();

        for (GUIItem item : ConfigData.fillerItems) {
            for (int slot : item.getSlots()) {
                inv.setItem(slot, item.getItem());
            }
        }

        for (Upgrade upgrade : upgradeList) {
            ItemStack icon = upgrade.getIcon().clone();
            ItemMeta meta = icon.getItemMeta();
            List<Component> lore = new ArrayList<>();
            int level = NBT.get(mainHand, nbt -> (int) nbt.getInteger("upgrade." + upgrade.getName())); // nbt.getInt("upgrade." + upgrade.getName());
            for (String line : upgrade.getLore()) {
            	lore.add(Main.mm.deserialize(line,
            			Placeholder.parsed("cost", level == upgrade.getMaxLevel() ? ConfigData.maxedOutGUIText : ConfigData.costString.replaceAll("<cost>", String.valueOf((int) upgrade.getCost(level).doubleValue()))),
            			Placeholder.parsed("multiplier", df.format(upgrade.getMultiplier(level))),
            			Placeholder.parsed("level", String.valueOf(level)),
            			Placeholder.parsed("progress", MiscUtils.getStatusBar(level, upgrade.getMaxLevel())),
            			Placeholder.parsed("multiplier_chance", df.format(upgrade.getMultiplier(level) * 100)),
            			Placeholder.parsed("max-level", String.valueOf(upgrade.getMaxLevel()))
            	).decoration(TextDecoration.ITALIC, false));
            }
            meta.lore(lore);
            icon.setItemMeta(meta);

            NBT.modify(icon, iconNBT -> {
            	iconNBT.setInteger("upgrade." + upgrade.getName(), level);
            	iconNBT.setString("upgrade.name", upgrade.getName());
            });

            inv.setItem(upgrade.getSlot(), icon);
        }

        return inv;
    }

    public static Inventory getShopMenu() {
        Inventory inv = Bukkit.createInventory(null, ConfigData.shopRows * 9, MiscUtils.chat(ConfigData.shopMenuName));
        for (GUIItem item : ConfigData.shopFillerItems) {
            for (int slot : item.getSlots()) {
                inv.setItem(slot, item.getItem());
            }
        }

        for (ShopItem shopItem : Main.getShopManager().getShopItems()) {
            ItemStack item = shopItem.getItem().clone();
            ItemMeta meta = item.getItemMeta();
            List<Component> lore = new ArrayList<>();

            for (String line : meta.getLore())
            {
            	String message = line.replaceAll("<cost>", String.valueOf(shopItem.getPrice()));
                lore.add(MiscUtils.chat(message));
            }
            meta.lore(lore);
            item.setItemMeta(meta);
            inv.setItem(shopItem.getSlot(), item);
        }

        return inv;
    }
}
