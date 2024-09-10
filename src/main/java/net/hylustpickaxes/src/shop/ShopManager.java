package net.hylustpickaxes.src.shop;

import net.hylustpickaxes.src.Main;
import net.hylustpickaxes.src.utils.EnchantNames;
import net.hylustpickaxes.src.utils.ItemUtils;
import net.hylustpickaxes.src.utils.MiscUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShopManager {
    private Main instance;
    private File itemsFile = new File("plugins/HylustPickaxes/", "shopitems.yml");
    private FileConfiguration itemsData = YamlConfiguration.loadConfiguration(itemsFile);

    public List<ShopItem> getShopItems() {
        return shopItems;
    }

    private List<ShopItem> shopItems = new ArrayList<>();

    public ShopManager(Main instance) {
        this.instance = instance;
        loadItems();
    }

    public void saveDefaultItemData() {

        itemsData.set("shops", new ArrayList<String>());
        try {
            itemsData.save(itemsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        itemsData.set("shops.dirt.material", Material.QUARTZ.name());
        itemsData.set("shops.dirt.quantity", 1);
        itemsData.set("shops.dirt.damage", 0);
        itemsData.set("shops.dirt.name", "nope");
        itemsData.set("shops.dirt.lore", Arrays.asList(new String[]{"yahalloooo"}));
        itemsData.set("shops.dirt.slot", 0);
        itemsData.set("shops.dirt.price", 1);
        itemsData.set("shops.dirt.type", "COMMAND");
        itemsData.set("shops.dirt.commands", Arrays.asList(new String[]{"/say hi", "/kick %player%"}));


        try {
            itemsData.save(itemsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadItems() {
        if (itemsData.getConfigurationSection("shops") == null) {
            saveDefaultItemData();
            reloadItems();
            return;
        }
        for (String s : itemsData.getConfigurationSection("shops").getKeys(false)) {
            Material material;
            short data = 0;
            int amount = 1;
            String name = "";
            String type = "ITEM";
            String enchant = "";
            List<String> lore = new ArrayList<>();
            List<String> commands = new ArrayList<>();
            List<String> enchants = new ArrayList<>();

            // load itemstack based on data
            material = Material.getMaterial(itemsData.getString("shops." + s + ".material"));
            if (material == null) material = Material.DIRT;
            amount = itemsData.getInt("shops." + s + ".quantity");
            if (itemsData.getString("shops." + s + ".damage") != null)
                data = Short.parseShort(itemsData.getString("shops." + s + ".damage"));
            name = itemsData.getString("shops." + s + ".name");
            if (itemsData.getStringList("shops." + s + ".lore") != null)
                lore = itemsData.getStringList("shops." + s + ".lore");

            if (itemsData.getStringList("shops." + s + ".enchants") != null)
                enchants = itemsData.getStringList("shops." + s + ".enchants");

            int slot = itemsData.getInt("shops." + s + ".slot");
            int price = itemsData.getInt("shops." + s + ".price");

            if (itemsData.getString("shops." + s + ".type") != null) {
                type = itemsData.getString("shops." + s + ".type");
            }

            if (type.equals("COMMAND")) {
                commands = itemsData.getStringList("shops." + s + ".commands");
            }

            ItemStack itemStack = new ItemStack(material, amount, data);
            if (enchants != null) {
                for (String en : enchants) {
                    itemStack.addUnsafeEnchantment(EnchantNames.getEnchantment(en.split(":")[0]), Integer.parseInt(en.split(":")[1]));
                }
            }
            ItemMeta meta = itemStack.getItemMeta();
            if (name != null) meta.setDisplayName(MiscUtils.chat(name));
            if (lore != null) meta.setLore(lore);
            itemStack.setItemMeta(meta);

            ShopItem shopItem = new ShopItem(itemStack, price, slot);
            shopItem.setType(type);
            if (type.equals("COMMAND")) {
                shopItem.setCommands(commands);
            }
            shopItems.add(shopItem);

            ItemStack guiItem = ItemUtils.getItemStack(material, amount, data, name, lore);
            if (enchants != null) {
                for (String en : enchants) {
                    guiItem.addUnsafeEnchantment(EnchantNames.getEnchantment(en.split(":")[0]), Integer.parseInt(en.split(":")[1]));
                }
            }
            lore.add(MiscUtils.chat("&f&lPRICE: &f" + price + " coins"));
            if (lore != null) meta.setLore(lore);
            guiItem.setItemMeta(meta);
            
        }
    }


    public void reloadItems() {
        loadItems();
    }


}
