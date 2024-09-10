package net.hylustpickaxes.src.config;

import net.hylustpickaxes.src.Main;
import net.hylustpickaxes.src.gui.GUIItem;
import net.hylustpickaxes.src.utils.ItemUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigHandler {
    private static File file = new File("plugins/MoonlightPickaxes/", "config.yml");
    private static YamlConfiguration data = YamlConfiguration.loadConfiguration(file);

    public ConfigHandler(Main instance) {

        loadConfig();
    }

    public static void loadConfig() {

        ConfigData.keyChances = new HashMap<>();
        ConfigData.tokenPouchChances = new HashMap<>();

        // load key chances
        for (String key : data.getConfigurationSection("keychances").getKeys(false)) {
            Double chance = Double.valueOf(data.getDouble("keychances." + key));
            ConfigData.keyChances.put(key, chance);
        }

        // load pouch chances
        for (String pouch : data.getConfigurationSection("tokenpouches").getKeys(false)) {
            Double chance = Double.valueOf(data.getDouble("tokenpouches." + pouch));
            ConfigData.tokenPouchChances.put(pouch, chance);
        }


        if (data.getConfigurationSection("menu") != null)
        {
            ConfigData.upgradeMenuName = data.getString("menu.name");
            ConfigData.rows = data.getInt("menu.rows");
            ConfigData.fillerItems = new ArrayList<>();
            for (String icon : data.getConfigurationSection("menu.fills").getKeys(false))
            {
                ItemStack item = ItemUtils.getItemStackFromConfig(data, "menu.fills." + icon);
                List<Integer> slots = data.getIntegerList("menu.fills." + icon + ".slots");

                ConfigData.fillerItems.add(new GUIItem(item, slots));
            }
        }

        if (data.getConfigurationSection("shop") != null)
        {
            ConfigData.shopMenuName = data.getString("shop.name");
            ConfigData.shopRows = data.getInt("shop.rows");
            ConfigData.shopFillerItems = new ArrayList<>();
            for (String icon : data.getConfigurationSection("shop.fills").getKeys(false))
            {
                ItemStack item = ItemUtils.getItemStackFromConfig(data, "shop.fills." + icon);
                List<Integer> slots = data.getIntegerList("shop.fills." + icon + ".slots");

                ConfigData.shopFillerItems.add(new GUIItem(item, slots));
            }
        }

        ConfigData.bars = data.getInt("placeholder.bars");
        ConfigData.filled = data.getString("placeholder.filled");
        ConfigData.notFilled = data.getString("placeholder.non-filled");
        ConfigData.costString = data.getString("placeholder.cost");
        ConfigData.maxedOutGUIText = data.getString("placeholder.maxed-out");

        ConfigData.noPerm = data.getString("messages.no_permission");
        ConfigData.invalidNumber = data.getString("messages.invalid-number");
        ConfigData.invalidCmdUsage = data.getString("messages.invalid-command-usage");
        ConfigData.playerOnly = data.getString("messages.player-only");
        ConfigData.reload = data.getString("messages.reload");
        ConfigData.notOnline = data.getString("messages.player-not-online");
        ConfigData.maxedOut = data.getString("messages.maxed-out");
        ConfigData.notEnoughTokens = data.getString("messages.not-enough-tokens");
        ConfigData.enchantBought = data.getString("messages.enchant-bought");
        ConfigData.getTokensMessage = data.getString("messages.get-tokens");
        ConfigData.shopBought = data.getString("messages.shop-bought-msg");
        ConfigData.playerInvalid = data.getString("messages.invalid-player");
        ConfigData.cooldown = data.getString("messages.cooldown-msg");

        ConfigData.accessUpgradeMenu = data.getString("features.accessUpgradeMenu");
    }
}
