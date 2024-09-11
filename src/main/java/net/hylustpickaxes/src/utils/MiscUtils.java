package net.hylustpickaxes.src.utils;

import net.hylustpickaxes.src.Main;
import net.hylustpickaxes.src.config.ConfigData;
import net.hylustpickaxes.src.upgrades.Upgrade;
import net.kyori.adventure.text.Component;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBT;

import java.util.*;

public class MiscUtils {
    public static Component chat(String message) {
    	return Main.mm.deserialize(message);
    }

    public static boolean getBooleanFromWeightedChance(double chance, Random random) {
        Map<String, Double> yesNoMap = new HashMap<>();
        yesNoMap.put("yes", chance);
        yesNoMap.put("no", 1 - chance);
        return (getWeightedRandom(yesNoMap, random).equals("yes"));
    }

    public static <E> E getWeightedRandom(Map<E, Double> weights, Random random) {
        E result = null;
        double bestValue = Double.MAX_VALUE;

        for (E element : weights.keySet()) {
            double value = -Math.log(random.nextDouble()) / weights.get(element);

            if (value < bestValue) {
                bestValue = value;
                result = element;
            }
        }

        return result;
    }

    public static String getStatusBar(int currentLevel, int maxLevel) {
        String bar = "";
        int amountOfFilledBars = (int) ((1.0 * currentLevel / maxLevel) * ConfigData.bars);
        for (int i = 0; i < amountOfFilledBars; i++) {
            bar += ConfigData.filled;
        }

        for (int i = 0; i < ConfigData.bars - amountOfFilledBars; i++) {
            bar += ConfigData.notFilled;
        }
        return bar;
    }

    public static List<Material> getOres() {
        List<Material> oresList = new ArrayList<>();
        oresList.add(Material.COAL_ORE);
        oresList.add(Material.IRON_ORE);
        oresList.add(Material.LAPIS_ORE);
        oresList.add(Material.GOLD_ORE);
        oresList.add(Material.DIAMOND_ORE);
        oresList.add(Material.REDSTONE_ORE);
        oresList.add(Material.EMERALD_ORE);
        oresList.add(Material.NETHER_QUARTZ_ORE);
        return oresList;
    }

    public static ItemStack getOreDrops(Material material, boolean isAutoSmelt, int fortuneLevel) {
        Random random = new Random();
        switch (material) {
            case COAL_ORE:
                return new ItemStack(Material.COAL, random.nextInt(fortuneLevel + 1) + 1);
            case IRON_ORE:
                if (isAutoSmelt)
                    return new ItemStack(Material.IRON_INGOT, random.nextInt(fortuneLevel + 1) + 1);
                else
                    return new ItemStack(Material.IRON_ORE, random.nextInt(fortuneLevel + 1) + 1);
            case GOLD_ORE:
                if (isAutoSmelt)
                    return new ItemStack(Material.GOLD_INGOT, random.nextInt(fortuneLevel + 1) + 1);
                else
                    return new ItemStack(Material.GOLD_ORE, random.nextInt(fortuneLevel + 1) + 1);
            case LAPIS_ORE:
                return new ItemStack(Material.LAPIS_LAZULI, random.nextInt(fortuneLevel + 4) + 1);
            case DIAMOND_ORE:
                return new ItemStack(Material.DIAMOND, random.nextInt(fortuneLevel + 1) + 1);
            case REDSTONE_ORE:
                return new ItemStack(Material.REDSTONE, random.nextInt(fortuneLevel + 1) + 1);
            case EMERALD_ORE:
                return new ItemStack(Material.EMERALD, random.nextInt(fortuneLevel + 1) + 1);
            case NETHER_QUARTZ_ORE:
                return new ItemStack(Material.QUARTZ, random.nextInt(fortuneLevel + 1) + 1);
            default:
                return null;
        }
    }

    public static List<Block> getAllOreBlocksInRadius(Block block, int range)
    {
        Location c1 = block.getLocation().subtract(range, range, range);
        Location c2 = block.getLocation().add(range, range, range);
        return getOreBlocksFromCorners(c1, c2);
    }

    public static List<Block> getOreBlocksFromCorners(Location corner1, Location corner2) {
        double minX = Math.min(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());
        List<Block> oreBlockList = new ArrayList<>();
        double x;
        for (x = minX; x <= maxX; x++) {
            double y;
            for (y = minY; y <= maxY; y++) {
                double z;
                for (z = minZ; z <= maxZ; z++) {
                    Block checkBlock = (new Location(corner1.getWorld(), x, y, z)).getBlock();
                    if (getOres().contains(checkBlock.getType())) {
                        oreBlockList.add(checkBlock);
                    }
                }
            }
        }
        return oreBlockList;
    }

    public static List<UUID> getPlayersInRange(Player pl, int range)
    {
        List<UUID> uuids = new ArrayList<>();
        for (Entity e : pl.getNearbyEntities(range, range, range)){
            if (e instanceof Player){
                uuids.add(e.getUniqueId());
            }
        }
        return uuids;
    }

    public static HashMap<Upgrade, Integer> getAllUpgradeLevels(List<Upgrade> upgrades, ItemStack item)
    {
        HashMap<Upgrade, Integer> upgradeMap = new HashMap<>();
        NBT.get(item, nbt -> {
	        for (Upgrade upgrade : upgrades)
	        {
	            int level = nbt.getOrDefault("upgrade." + upgrade.getName(), 0);
	            if (level > upgrade.getMaxLevel()) level = upgrade.getMaxLevel();
	            upgradeMap.put(upgrade, level);
	        }
        });
        return upgradeMap;
    }

}
