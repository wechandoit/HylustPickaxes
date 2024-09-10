package net.hylustpickaxes.src.gui;

import net.hylustpickaxes.src.Main;
import net.hylustpickaxes.src.config.ConfigData;
import net.hylustpickaxes.src.nbt.NBT;
import net.hylustpickaxes.src.shop.ShopItem;
import net.hylustpickaxes.src.upgrades.Upgrade;
import net.hylustpickaxes.src.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        NBT nbt = NBT.get(player.getItemInHand());
        for (GUIItem item : ConfigData.fillerItems) {
            for (int slot : item.getSlots()) {
                inv.setItem(slot, item.getItem());
            }
        }

        for (Upgrade upgrade : upgradeList) {
            ItemStack icon = upgrade.getIcon().clone();
            ItemMeta meta = icon.getItemMeta();
            List<String> lore = new ArrayList<>();
            int level = nbt.getInt("upgrade." + upgrade.getName());
            for (String line : meta.getLore()) {
                lore.add(MiscUtils.chat(line.replaceAll("<cost>", level == upgrade.getMaxLevel() ? ConfigData.maxedOutGUIText : ConfigData.costString.replaceAll("<cost>", String.valueOf((int) upgrade.getCost(level).doubleValue()))).replaceAll("<multiplier>", df.format(upgrade.getMultiplier(level)))
                        .replaceAll("<level>", String.valueOf(level)).replaceAll("<progress>", MiscUtils.getStatusBar(level, upgrade.getMaxLevel())))
                        .replaceAll("<multiplier_chance>", df.format(upgrade.getMultiplier(level) * 100))
                        .replaceAll("<max-level>", String.valueOf(upgrade.getMaxLevel())));
            }
            meta.setLore(lore);
            icon.setItemMeta(meta);

            NBT iconNBT = NBT.get(icon);
            iconNBT.setInt("upgrade." + upgrade.getName(), level);
            iconNBT.setString("upgrade.name", upgrade.getName());

            inv.setItem(upgrade.getSlot(), iconNBT.apply(icon));
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
            List<String> lore = new ArrayList<>();

            for (String line : meta.getLore())
            {
                lore.add(MiscUtils.chat(line).replaceAll("<cost>", String.valueOf(shopItem.getPrice())));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(shopItem.getSlot(), item);
        }

        return inv;
    }
}
