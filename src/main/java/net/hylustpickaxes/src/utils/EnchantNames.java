package net.hylustpickaxes.src.utils;

import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum EnchantNames
{
    ARROW_DAMAGE(new String[] { "power" }),
    ARROW_FIRE(new String[] { "firearrow", "flame" }),
    ARROW_INFINITE(new String[] { "infinity", "infinitearrow" }),
    ARROW_KNOCKBACK(new String[] { "punch", "knockbackarrow" }),
    BINDING_CURSE(new String[] { "binding", "curseofbinding" }),
    CHANNELING(new String[0]),
    DAMAGE_ALL(new String[] { "sharpness", "alldamage", "dmg" }),
    DAMAGE_ARTHROPODS(new String[] { "baneofarthropods", "arthropodsdamage" }),
    DAMAGE_UNDEAD(new String[] { "smite", "undeaddamage" }),
    DEPTH_STRIDER(new String[] { "depthstrider", "striderdepth", "dstrider" }),
    DIG_SPEED(new String[] { "efficiency" }),
    DURABILITY(new String[] { "unbreaking" }),
    FIRE_ASPECT(new String[] { "fire" }),
    FROST_WALKER(new String[] { "frost" }),
    IMPALING(new String[0]),
    KNOCKBACK(new String[] { "knock" }),
    LOYALTY(new String[0]),
    LOOT_BONUS_BLOCKS(new String[] { "fortune", "lootbonusblock", "blockslootbonus" }),
    LOOT_BONUS_MOBS(new String[] { "looting", "lootbonusmob", "mobslootbonus" }),
    LUCK(new String[] { "luckofthesea", "luckofsea" }),
    LURE(new String[] { "luring" }),
    MENDING(new String[] { "mend" }),
    MULTISHOT(new String[0]),
    OXYGEN(new String[] { "respiration", "waterbreathing", "breathing" }),
    PIERCING(new String[0]),
    PROTECTION_ENVIRONMENTAL(new String[] { "protection", "protect", "prot" }),
    PROTECTION_EXPLOSIONS(new String[] { "blastprotection", "explosionsprotection", "expprotect", "expprot" }),
    PROTECTION_FALL(new String[] { "featherfalling", "fallprotection", "fallprotect", "fallprot" }),
    PROTECTION_FIRE(new String[] { "fireprotection", "fireprotect", "fireprot" }),
    PROTECTION_PROJECTILE(new String[] { "projectileprotection", "projectileprotect", "projectileprot" }),
    QUICK_CHARGE(new String[0]),
    RIPTIDE(new String[0]),
    SILK_TOUCH(new String[] { "silk" }),
    SWEEPING_EDGE(new String[] { "sweeping" }),
    THORNS(new String[] { "thorn" }),
    VANISHING_CURSE(new String[] { "vanishing", "curseofvanishing" }),
    WATER_WORKER(new String[] { "aquaaffinity" });

    private List<String> names = new ArrayList<String>();


    EnchantNames(String... paramVarArgs1) { this.names.addAll(Arrays.asList(paramVarArgs1)); }


    public static Enchantment getEnchantment(String paramString) {
        for (EnchantNames enchantmentNames : values()) {
            if (enchantmentNames.name().replace("_", "").equalsIgnoreCase(paramString.replace("_", ""))) {
                return Enchantment.getByName(enchantmentNames.name().toUpperCase());
            }

            for (String str : enchantmentNames.getNames()) {
                if (paramString.replace("_", "").equalsIgnoreCase(str)) {
                    return Enchantment.getByName(enchantmentNames.name().toUpperCase());
                }
            }
        }

        throw new IllegalArgumentException("Invalid enchantment name \"" + paramString + "\"");
    }


    private List<String> getNames() { return this.names; }
}