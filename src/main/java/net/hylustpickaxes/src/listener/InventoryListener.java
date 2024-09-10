package net.hylustpickaxes.src.listener;

import net.hylustpickaxes.src.Main;
import net.hylustpickaxes.src.config.ConfigData;
import net.hylustpickaxes.src.gui.GUIManager;
import net.hylustpickaxes.src.profiles.Profile;
import net.hylustpickaxes.src.shop.ShopItem;
import net.hylustpickaxes.src.tools.Tool;
import net.hylustpickaxes.src.upgrades.Upgrade;
import net.hylustpickaxes.src.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBT;

public class InventoryListener implements Listener {
    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        Inventory inventory = event.getInventory();
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR && Main.getToolManger().itemIsTool(itemStack)) {
                event.getInventory().setResult(null);
            }
        }
    }

    @EventHandler
    public void onCraft(PrepareItemEnchantEvent event) {
        ItemStack itemStack = event.getItem();
        if (itemStack == null || !Main.getToolManger().itemIsTool(itemStack)) {
            return;
        }
        event.getItem().setAmount(0);;
    }

    @EventHandler
    public static void onItemDrop(PlayerDropItemEvent event) {
        Player player = (Player) event.getPlayer();
        ItemStack hand = event.getItemDrop().getItemStack();
        if (player == null || player.isDead() || event.getItemDrop() == null) return;
        if (Main.getToolManger().itemIsTool(hand)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public static void onItemMove(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack hand = event.getCursor();
        ItemStack clickedItem = event.getCurrentItem();
        if ((player == null) || player.isDead()) return;
        if (!((hand == null) || (hand.getType() == Material.AIR))) {
            if (Main.getToolManger().itemIsTool(hand)) {
                if (event.getClickedInventory() == null || !event.getClickedInventory().equals(player.getInventory()) ||
                        event.getClick().isShiftClick() || event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                    event.setCancelled(true);
                }
            }
        } else if (clickedItem != null && clickedItem.getType() != Material.AIR && Main.getToolManger().itemIsTool(clickedItem)) {
            if (event.getClick().isShiftClick() || event.getClick() == ClickType.NUMBER_KEY) {
                event.setCancelled(true);
            }
        } else if (event.getClick() == ClickType.NUMBER_KEY){
            if (player.getInventory().getContents()[event.getHotbarButton()] != null)
            {
                ItemStack hotbar = player.getInventory().getContents()[event.getHotbarButton()];
                if (hotbar != null && hotbar.getType() != Material.AIR && Main.getToolManger().itemIsTool(hotbar))
                {
                    event.setCancelled(true);
                }
            }
        } else {
            return;
        }
    }

    @EventHandler
    public static void onShopInvClick(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();
        Profile profile = Main.getProfileManager().getProfile(player);
        Inventory inventory = event.getClickedInventory();
        if (inventory != null && event.getView().title() != null && event.getView().title().equals(MiscUtils.chat(ConfigData.shopMenuName)))
        {
            event.setCancelled(true);
            for (ShopItem shopItem : Main.getShopManager().getShopItems())
            {
                if (event.getRawSlot() == shopItem.getSlot())
                {
                    switch (shopItem.getType())
                    {
                        case "COMMAND":
                            player.sendMessage(MiscUtils.chat(ConfigData.shopBought.replaceAll("<item>", shopItem.getItem().getItemMeta().getDisplayName() + "&r").replaceAll("<cost>", String.valueOf(shopItem.getPrice()))));
                            for (String command : shopItem.getCommands())
                            {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                            }
                            break;
                        default:
                            player.sendMessage(MiscUtils.chat(ConfigData.shopBought.replaceAll("<item>", shopItem.getItem().getItemMeta().getDisplayName() + "&r").replaceAll("<cost>", String.valueOf(shopItem.getPrice()))));

                            if (player.getInventory().firstEmpty() != -1)
                            {
                                player.getInventory().addItem(shopItem.getItem());
                            } else
                            {
                                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), shopItem.getItem());
                            }
                    }
                    profile.setTokens((int) (profile.getTokens("pickaxe") - shopItem.getPrice()), "pickaxe");
                    Main.getProfileManager().saveData();
                }
            }
        } else
        {
            return;
        }
    }

    @EventHandler
    public static void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack hand = player.getInventory().getItemInMainHand();
        Profile profile = Main.getProfileManager().getProfile(player);
        try {
            Inventory inventory = event.getClickedInventory();
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() == Material.AIR || player == null || player.isDead())
                return;
            if (event.getView().title().equals(MiscUtils.chat(ConfigData.upgradeMenuName))) {
                event.setCancelled(true);
                if (!NBT.get(item, nbt -> (String) nbt.getString("upgrade.name")).equals("") && NBT.get(item, nbt -> (String) nbt.getString("upgrade.name")) != null) {
                    String upgradeName = NBT.get(item, nbt -> (String) nbt.getString("upgrade.name"));
                    int level = NBT.get(item, nbt -> (int) nbt.getInteger("upgrade." + upgradeName));
                    Upgrade upgrade = Main.getUpgradeManager().getUpgrade(upgradeName);
                    Tool tool = Main.getToolManger().getTool(hand);
                    String tokenName = tool.getTokenName();
                    int tokens = profile.getTokens(tokenName);
                    if (level == upgrade.getMaxLevel()) {
                        player.sendMessage(MiscUtils.chat(ConfigData.maxedOut));
                        return;
                    }
                    if (upgrade.getCost(level) > tokens) {
                        player.sendMessage(MiscUtils.chat(ConfigData.notEnoughTokens));
                    } else {
                        player.sendMessage(MiscUtils.chat(ConfigData.enchantBought.replaceAll("<enchant>", upgrade.getName()).replaceAll("<amount>", ConfigData.costString.replaceAll("<cost>", String.valueOf(upgrade.getCost(level))))));
                        profile.removeTokens((int) upgrade.getCost(level).doubleValue(), tokenName);
                        NBT.modify(hand, nbt -> {
                        	nbt.setInteger("upgrade." + upgradeName, level + 1);
                        });
                        player.getInventory().setItemInMainHand(hand);
                        Main.getProfileManager().saveData();

                        player.openInventory(GUIManager.getUpgradeMenu(tool.getUpgrades(), player));
                    }
                }
            }
        } catch (NullPointerException e) {
            return;
        }
    }
}
