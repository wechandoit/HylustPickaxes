package net.hylustpickaxes.src.upgrades;

import net.hylustpickaxes.src.utils.ItemUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UpgradeManager {

    private List<Upgrade> upgrades;
    private static File file = new File("plugins/MoonlightPickaxes/", "config.yml");
    private static YamlConfiguration data = YamlConfiguration.loadConfiguration(file);

    public UpgradeManager() {
        this.upgrades = new ArrayList<>();
        retrieveData();
    }

    public void retrieveData() {
        if (data.getConfigurationSection("upgrade") == null) {
            return;
        }
        for (String name : data.getConfigurationSection("upgrade").getKeys(false)) {
            int maxLevel = data.getInt("upgrade." + name + ".max-level");
            int slot = data.getInt("upgrade." + name + ".slot");
            List<Double> costs = new ArrayList<>();
            List<Double> multipliers = new ArrayList<>();
            List<Double> values = new ArrayList<>();
            ItemStack icon = ItemUtils.getItemStackFromConfig(data, "upgrade." + name);

            for (int i = 1; i <= maxLevel; i++)
            {
                costs.add(data.getDouble("upgrade." + name + ".prices." + i + ".cost"));
                if (data.getDouble("upgrade." + name + ".prices." + i + ".multiplier") != 0) multipliers.add(data.getDouble("upgrade." + name + ".prices." + i + ".multiplier"));
                else multipliers.add((double) i);

                if (data.getDouble("upgrade." + name + ".prices." + i + ".value") != 0) values.add(data.getDouble("upgrade." + name + ".prices." + i + ".value"));
                else values.add((double) i);
            }

            List<String> functions = data.getStringList("upgrade." + name + ".functions");
            String type = data.getString("upgrade." + name + ".type");

            upgrades.add(new Upgrade(maxLevel, name, costs, multipliers, values, icon, slot, functions, type));
        }
        System.out.println(upgrades.size() + " Upgrades Loaded!");
    }

    public Upgrade getUpgrade(String upgradeID)
    {
        if (upgrades.isEmpty() || upgrades == null) return null;
        for (Upgrade upgrade : upgrades)
        {
            if (upgrade.getName().equalsIgnoreCase(upgradeID)) return upgrade;
        }
        return null;
    }
}
