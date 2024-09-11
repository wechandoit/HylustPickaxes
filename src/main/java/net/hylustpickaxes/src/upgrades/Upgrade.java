package net.hylustpickaxes.src.upgrades;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Upgrade {
    private int minLevel = 1;
    private int maxLevel;
    private String name;
    private List<Double> costs;
    private List<Double> multiplier;
    private List<Double> values;
    private ItemStack icon;
    private List<String> lore;
    private int slot;
    private List<String> function;
    private String type;

    public Upgrade(int maxLevel, String name, List<Double> costs, List<Double> multiplier, List<Double> values, ItemStack icon, List<String> lore, int slot, List<String> function, String type) {
        this.maxLevel = maxLevel;
        this.name = name;
        this.costs = costs;
        this.multiplier = multiplier;
        this.values = values;
        this.icon = icon;
        this.lore = lore;
        this.slot = slot;
        this.function = function;
        this.type = type;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue(int level)
    {
        if (level != 0) return values.get(level - 1);
        return Double.valueOf(0);
    }

    public List<Double> getCosts() {
        return costs;
    }

    public Double getCost(int level) {
        if (level < maxLevel) return costs.get(level);
        else return Double.valueOf(0);
    }

    public void setCosts(List<Double> costs) {
        this.costs = costs;
    }

    public Double getMultiplier(int level) {
        if (level != 0) return multiplier.get(level - 1);
        return Double.valueOf(0);
    }

    public void setMultiplier(Double value, int level) {
        multiplier.set(level, value);
    }

    public ItemStack getIcon() {
        return icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }
    
    public List<String> getLore() {
    	return lore;
    }

    public List<String> getFunction() {
        return function;
    }

    public void setFunction(List<String> function) {
        this.function = function;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
