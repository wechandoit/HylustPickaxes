package net.hylustpickaxes.src.tools;

import net.hylustpickaxes.src.Main;
import net.hylustpickaxes.src.nbt.NBT;
import net.hylustpickaxes.src.upgrades.Upgrade;
import net.hylustpickaxes.src.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ToolManger {
    private List<Tool> tools = new ArrayList<Tool>();
    private List<String> tokenTypes = new ArrayList<>();
    private static File file = new File("plugins/MoonlightPickaxes/", "config.yml");
    private static YamlConfiguration toolConfig = YamlConfiguration.loadConfiguration(file);

    public ToolManger() {

        retrieveData();
    }

    public List<String> getTokenTypes() {
        return tokenTypes;
    }

    public void setTokenTypes(List<String> tokenTypes) {
        this.tokenTypes = tokenTypes;
    }

    public void retrieveData() {
        if (toolConfig.getConfigurationSection("item") == null) return;
        for (String type : toolConfig.getConfigurationSection("item").getKeys(false)) {
            String tokenName = toolConfig.getString("item." + type + ".tokenname");

            // get upgrades from id
            List<Upgrade> upgrades = new ArrayList<>();
            if (toolConfig.get("item." + type + ".upgrades") != null)
            {
                for (String toolUpgrade : toolConfig.getStringList("item." + type + ".upgrades"))
                {
                    Upgrade upgrade = Main.getUpgradeManager().getUpgrade(toolUpgrade);
                    if (upgrade != null) upgrades.add(upgrade);
                }
            }
            //gen item

            ItemStack item = ItemUtils.getItemStackFromConfig(toolConfig, "item." + type);
            NBT toolNBT = NBT.get(item);
            toolNBT.setBoolean("Unbreakable", true);
            toolNBT.setString("type", tokenName);
            toolNBT.setString("isTool", "yes");
            toolNBT.setInt("value", 0);
            toolNBT.setString("totalSold", "0.0");
            toolNBT.setString("multiplier", "1.0");

            for (Upgrade upgrade : upgrades)
            {
                toolNBT.setInt("upgrade." + upgrade.getName(), 0);
            }

            item = toolNBT.apply(item);

            // create tool
            tools.add(new Tool(item, upgrades, tokenName));
            tokenTypes.add(tokenName);
        }
        System.out.println(tools.size() + " tools loaded!");
    }

    public Tool getTool(ItemStack itemStack)
    {
        if (tools.isEmpty() || tools == null) return null;
        NBT nbt = NBT.get(itemStack);
        String tokenName = nbt.getString("type");
        for (Tool tool : tools)
        {
            if (tool.getTokenName().equalsIgnoreCase(tokenName)) return tool;
        }
        return null;
    }

    public Tool getTool(String tokenName)
    {
        if (tools.isEmpty() || tools == null) return null;
        for (Tool tool : tools)
        {
            if (tool.getTokenName().equals(tokenName)) return tool;
        }
        return null;
    }

    public List<Tool> getPickaxeList() {
        List<Tool> pickList = new ArrayList<Tool>();
        if (tools.isEmpty() || tools == null) return null;
        for (Tool tool : tools) {
            if (tool.getItem(0, 0, 0, null).getType().equals(Material.DIAMOND_PICKAXE) || tool.getItem(0, 0,0, null).getType().equals(Material.IRON_PICKAXE) || tool.getItem(0, 0, 0, null).getType().equals(Material.GOLD_PICKAXE) || tool.getItem(0, 0, 0, null).getType().equals(Material.STONE_PICKAXE) || tool.getItem(0, 0,0, null).getType().equals(Material.WOOD_PICKAXE)) {
                pickList.add(tool);
            }
        }
        return pickList;
    }

    public boolean isToolPickaxe(ItemStack itemStack)
    {
        if (getPickaxeList().isEmpty() || getPickaxeList() == null) return false;
        NBT nbt = NBT.get(itemStack);
        String tokenName = nbt.getString("type");
        for (Tool tool : getPickaxeList())
        {
            if (tool.getTokenName().equals(tokenName)) return true;
        }
        return false;
    }

    public boolean itemIsTool(ItemStack item)
    {
        if (tools.isEmpty() || tools == null || item == null) return false;
        if (NBT.get(item) == null)
            return false;
        NBT nbt = NBT.get(item);
        return nbt.getString("isTool").equals("yes");
    }
}
