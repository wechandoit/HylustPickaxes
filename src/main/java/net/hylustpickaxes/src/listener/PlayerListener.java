package net.hylustpickaxes.src.listener;

import net.hylustpickaxes.src.Main;
import net.hylustpickaxes.src.config.ConfigData;
import net.hylustpickaxes.src.gui.GUIManager;
import net.hylustpickaxes.src.profiles.Profile;
import net.hylustpickaxes.src.tools.Tool;
import net.hylustpickaxes.src.upgrades.Upgrade;
import net.hylustpickaxes.src.utils.MiscUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.tr7zw.nbtapi.NBT;

import java.util.List;
import java.util.UUID;

public class PlayerListener implements Listener {

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Profile profile = new Profile(player.getUniqueId());
        if (Main.getProfileManager().getProfile(player) == null) {
            Main.getProfileManager().getProfiles().add(profile);
        }
    }

    @EventHandler
    public static void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand == null || hand.getType() == Material.AIR || player == null || player.isDead() || !(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) || !player.isSneaking())
            return;
        if (Main.getToolManger().itemIsTool(hand) && Main.getToolManger().isToolPickaxe(hand) && ConfigData.accessUpgradeMenu.equalsIgnoreCase("both") || ConfigData.accessUpgradeMenu.equalsIgnoreCase("shift-right")) {
            Tool tool = Main.getToolManger().getTool(hand);
            if (tool != null) player.openInventory(GUIManager.getUpgradeMenu(tool.getUpgrades(), player));
        }
    }

    @EventHandler
    public void onGrenadeLaunch(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand == null || hand.getType() == Material.AIR || player == null || player.isDead() || !(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) || player.isSneaking())
            return;
        if (Main.getToolManger().itemIsTool(hand)) {

            if (Main.getInstance().grenadeCDTime.containsKey(uuid))
            {
            	String message = ConfigData.cooldown.replace("<time>", String.valueOf(Main.getInstance().grenadeCDTime.get(uuid)));
                player.sendMessage(MiscUtils.chat(message));
                return;
            }

            Tool tool = Main.getToolManger().getTool(hand);
            List<Upgrade> upgrades = tool.getUpgrades();

            for (Upgrade upgrade : upgrades) {
                int level = NBT.get(hand, nbt -> (int) nbt.getInteger("upgrade." + upgrade.getName()));
                if (level == 0) continue;
                else if (level > upgrade.getMaxLevel()) {
                    level = upgrade.getMaxLevel();
                    NBT.modify(hand, nbt -> {
                    	nbt.setInteger("upgrade." + upgrade.getName(), upgrade.getMaxLevel());
                    	});
                    player.getInventory().setItemInMainHand(hand);
                } else {
                    String[] line = upgrade.getType().split(":");
                    if (line[0].equalsIgnoreCase("GRENADE")) {
                        shoot(player);
                        if (!player.isOp()) Main.getInstance().grenadeCDTime.put(uuid, upgrade.getMultiplier(level).intValue());
                    }
                }
            }
        }
    }


    private void shoot(Player player) {
        Location eye = player.getEyeLocation();
        Location loc = eye.add(eye.getDirection().multiply(1.2));
        Fireball fireball = (Fireball) loc.getWorld().spawnEntity(loc, EntityType.FIREBALL);
        fireball.setVelocity(loc.getDirection().normalize().multiply(2));
        fireball.setShooter(player);
        fireball.setIsIncendiary(false);
        fireball.setYield(4);
        Main.fireballs.add(fireball);
    }

    @EventHandler
    public static void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();
        Profile profile = Main.getProfileManager().getProfile(player);
        if (hand == null || hand.getType() == Material.AIR || player == null || player.isDead())
            return;
        if (Main.getToolManger().itemIsTool(hand) && Main.getToolManger().isToolPickaxe(hand)) {
            Tool tool = Main.getToolManger().getTool(hand);
            for (Upgrade upgrade : tool.getUpgrades()) {
                String[] line = upgrade.getType().split(":");
                if (line.length > 1 && line[0].equalsIgnoreCase("potionEffect")) {
                    if (line[1].equalsIgnoreCase("haste")) {
                        int level =  NBT.get(hand, handNBT -> (int) handNBT.getInteger("upgrade." + upgrade.getName()));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, Integer.MAX_VALUE, level - 1));
                    }
                }
            }
        } else {
            player.removePotionEffect(PotionEffectType.HASTE);
        }
    }


}
