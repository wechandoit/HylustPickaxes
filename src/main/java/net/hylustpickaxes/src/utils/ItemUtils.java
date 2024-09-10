package net.hylustpickaxes.src.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.hylustpickaxes.src.Main;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemUtils {

    public static ItemStack getItemStack(Material material, int amount, int data, String name, List<String> lore) {
        if (material == null) material = Material.COBBLESTONE;
        ItemStack itemStack = new ItemStack(material, amount, (short) data);
        return getItemStack(itemStack, name, lore);
    }

    public static ItemStack getItemStack(Material material, int amount, int data, String name, List<String> lore, boolean hideEnchants) {
        if (material == null) material = Material.COBBLESTONE;
        ItemStack itemStack = new ItemStack(material, amount, (short) data);
        return getItemStack(itemStack, name, lore, hideEnchants);
    }

    public static ItemStack getItemStack(ItemStack itemStack, String name, List<String> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        if (name != null && !name.equals("")) meta.setDisplayName(MiscUtils.chat(name));
        if (lore != null || !lore.isEmpty()) {
            for (String s : lore) s = MiscUtils.chat(s);
            meta.setLore(lore);
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getItemStack(ItemStack itemStack, String name, List<String> lore, boolean hideEnchants) {
        ItemMeta meta = itemStack.getItemMeta();
        if (name != null && !name.equals("")) meta.setDisplayName(MiscUtils.chat(name));
        if (lore != null || !lore.isEmpty()) {
            for (String s : lore) s = MiscUtils.chat(s);
            meta.setLore(lore);
            if (hideEnchants) meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getSkullFromBase64(String base64) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        notNull(base64, "base64");

        UUID hashAsId = new UUID(base64.hashCode(), base64.hashCode());
        return Bukkit.getUnsafe().modifyItemStack(item,
                "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}");
    }

    public static ItemStack getSkullFromName(String name) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        notNull(name, "name");

        return Bukkit.getUnsafe().modifyItemStack(item,
                "{SkullOwner:\"" + name + "\"}"
        );
    }

    public static ItemStack getSkullFromURL(String url) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        if (url == null || url.isEmpty())
            return skull;
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        profileField.setAccessible(true);
        try {
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }

    private static void notNull(Object o, String name) {
        if (o == null) {
            throw new NullPointerException(name + " should not be null!");
        }
    }

    public static List<Block> getSurroundingSugarCane(Block block) {
        List<Block> blocks = new ArrayList<Block>();
        blocks.add(block);
        Location checkLoc = block.getLocation().clone().add(0.0D, 1.0D, 0.0D);
        while (checkLoc.getBlock().getType() == Material.SUGAR_CANE) {

            blocks.add(checkLoc.getBlock());
            checkLoc.add(0.0D, 1.0D, 0.0D);
        }
        return blocks;
    }

    public static ItemStack getItemStackFromConfig(YamlConfiguration config, String path) {
        Material material = Material.getMaterial(config.getString(path + ".material"));
        if (material == null) material = Material.BARRIER;
        String name = config.getString(path + ".name");
        int amount = 1;
        if (config.get(path + ".amount") != null)
        {
            amount = config.getInt(path + ".amount");
        } else if (config.get(path + ".maxAmount") != null)
        {
            amount = Main.random.nextInt(config.getInt(path + ".maxAmount")) + config.getInt(path + ".minAmount");
        }
        int damage = config.getInt(path + ".data");
        List<String> lore = new ArrayList<>();
        if (config.getStringList(path + ".lore") != null) lore = config.getStringList(path + ".lore");
        String url = "";
        if (config.getString(path + ".url") != null) url = config.getString(path + ".url");
        boolean hideEnchants = config.getBoolean(path + ".hideVanillaEnchants");
        if (material == Material.SKULL_ITEM && damage == 3 && !url.equals("")) {
            return getItemStack(getSkullFromURL(url), name, lore, hideEnchants);
        } else {
            return getItemStack(material, amount, damage, name, lore, hideEnchants);
        }
    }

}
