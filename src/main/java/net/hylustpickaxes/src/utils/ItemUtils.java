package net.hylustpickaxes.src.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.hylustpickaxes.src.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemUtils {
	
	private static final UUID RANDOM_UUID = UUID.randomUUID();

    public static ItemStack getItemStack(Material material, int amount, String name, List<Component> lore) {
        if (material == null) material = Material.COBBLESTONE;
        ItemStack itemStack = new ItemStack(material, amount);
        return getItemStack(itemStack, name, lore);
    }

    public static ItemStack getItemStack(Material material, int amount, String name, List<Component> lore, boolean hideEnchants) {
        if (material == null) material = Material.COBBLESTONE;
        ItemStack itemStack = new ItemStack(material, amount);
        return getItemStack(itemStack, name, lore, hideEnchants);
    }

    public static ItemStack getItemStack(ItemStack itemStack, String name, List<Component> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        if (name != null && !name.equals("")) meta.displayName(MiscUtils.chat(name).decoration(TextDecoration.ITALIC, false));
        if (lore != null || !lore.isEmpty()) {
            meta.lore(lore);
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getItemStack(ItemStack itemStack, String name, List<Component> lore, boolean hideEnchants) {
        ItemMeta meta = itemStack.getItemMeta();
        if (name != null && !name.equals("")) meta.displayName(MiscUtils.chat(name).decoration(TextDecoration.ITALIC, false));
        if (lore != null || !lore.isEmpty()) {
            meta.lore(lore);
            if (hideEnchants) meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getSkullFromBase64(String base64) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        notNull(base64, "base64");

        UUID hashAsId = new UUID(base64.hashCode(), base64.hashCode());
        return Bukkit.getUnsafe().modifyItemStack(item,
                "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}");
    }

    public static ItemStack getSkullFromName(String name) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        notNull(name, "name");
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwner(name);
        skull.setItemMeta(skullMeta);
        return skull;
    }
    
    private static PlayerProfile getProfile(String url) {
    	PlayerProfile profile = Bukkit.createProfile(RANDOM_UUID);
    	PlayerTextures textures = profile.getTextures();
    	URL urlObject;
    	try {
    		urlObject = new URL(url);
    	} catch (MalformedURLException exception) {
    		throw new RuntimeException("Invalid URL", exception);
    	}
    	textures.setSkin(urlObject);
    	profile.setTextures(textures);
    	return profile;
    }

    public static ItemStack getSkullFromURL(String url) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        if (url == null || url.isEmpty())
            return skull;
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        PlayerProfile profile = getProfile(url);
        skullMeta.setOwnerProfile(profile);
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
        List<Component> lore = new ArrayList<>();
        if (config.getStringList(path + ".lore") != null) {
        	for (String s : config.getStringList(path + ".lore")) {
        		lore.add(MiscUtils.chat(s));
        	}
        }
        String url = "";
        if (config.getString(path + ".url") != null) url = config.getString(path + ".url");
        boolean hideEnchants = config.getBoolean(path + ".hideVanillaEnchants");
        if (material == Material.PLAYER_HEAD && !url.equals("")) {
            return getItemStack(getSkullFromURL(url), name, lore, hideEnchants);
        } else {
            return getItemStack(material, amount, name, lore, hideEnchants);
        }
    }

}
