package net.hylustpickaxes.src.listener;

import dev.mrshawn.oreregenerator.api.utils.RegenUtils;
import net.brcdev.shopgui.ShopGuiPlusApi;
import net.hylustpickaxes.src.Main;
import net.hylustpickaxes.src.config.ConfigData;
import net.hylustpickaxes.src.events.DropPickaxeLootEvent;
import net.hylustpickaxes.src.events.TokensReceiveEvent;
import net.hylustpickaxes.src.nbt.NBT;
import net.hylustpickaxes.src.profiles.Profile;
import net.hylustpickaxes.src.tasktypes.OreBreakTaskType;
import net.hylustpickaxes.src.tools.Tool;
import net.hylustpickaxes.src.upgrades.Upgrade;
import net.hylustpickaxes.src.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class MiningListener implements Listener {
    private boolean mine(Player player, List<Block> oldBlockList) {
        if (player.isDead() || player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR || NBT.get(player.getItemInHand()) == null)
            return false;
        ItemStack pickaxe = player.getItemInHand();
        if (!Main.getToolManger().itemIsTool(pickaxe) || !Main.getToolManger().isToolPickaxe(pickaxe) || Main.getToolManger().getTool(pickaxe) == null)
            return false;

        Profile profile = Main.getProfileManager().getProfile(player);
        Tool tool = Main.getToolManger().getTool(pickaxe);
        NBT nbt = NBT.get(pickaxe);
        List<Upgrade> upgrades = tool.getUpgrades();

        HashMap<Upgrade, Integer> upgradeLevelMap = MiscUtils.getAllUpgradeLevels(upgrades, nbt);

        int amount = 1;
        double multi = 0;
        int value = nbt.getInt("value");
        double totalSold = Double.parseDouble(nbt.getString("totalSold"));
        boolean smelt = false;
        boolean autoSell = false;
        boolean nuke = false;
        int fortune = 0;
        int experience = 0;
        List<Block> blockList = new ArrayList<>();

        for (Block block : oldBlockList) {
            if (MiscUtils.getOres().contains(block.getType())) {
                for (Upgrade upgrade : upgradeLevelMap.keySet()) {
                    int level = upgradeLevelMap.get(upgrade);
                    nbt.setInt("upgrade." + upgrade.getName(), level);

                    switch (upgrade.getType().toUpperCase()) {
                        case "TRENCH":
                            if (MiscUtils.getBooleanFromWeightedChance(upgrade.getMultiplier(level), Main.random)) {
                                if (!nuke)
                                    blockList = MiscUtils.getAllOreBlocksInRadius(block, upgrade.getValue(level).intValue());
                            }
                            break;
                        case "NUKE":
                            if (MiscUtils.getBooleanFromWeightedChance(upgrade.getMultiplier(level), Main.random)) {
                                blockList = MiscUtils.getAllOreBlocksInRadius(block, upgrade.getValue(level).intValue());
                                nuke = true;
                            }
                            break;
                        case "COMMAND":
                            if (MiscUtils.getBooleanFromWeightedChance(upgrade.getMultiplier(level), Main.random)) {
                                for (String command : upgrade.getFunction()) {
                                    String keyType = MiscUtils.getWeightedRandom(ConfigData.keyChances, Main.random);
                                    String cmd = command.replaceAll("<player>", player.getName()).replaceAll("<key>", keyType);
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                                }
                            }
                            break;
                        case "TOKENFINDER":
                            if (MiscUtils.getBooleanFromWeightedChance(upgrade.getMultiplier(level), Main.random)) {
                                amount += upgrade.getValue(level);
                            }
                            break;
                        case "DAYLIGHTTOKENFINDER":
                            long time = player.getWorld().getTime();
                            if (time > 0 && time < 12300) {
                                if (MiscUtils.getBooleanFromWeightedChance(upgrade.getMultiplier(level), Main.random)) {
                                    amount += upgrade.getValue(level);
                                }
                            }
                            break;
                        case "EXPERIENCE":
                            experience += Main.random.nextInt(upgrade.getValue(level).intValue());
                            break;
                        case "AUTOSMELT":
                            smelt = true;
                            break;
                        case "AUTOSELL":
                            autoSell = true;
                            break;
                        case "CHARITY":
                            if (MiscUtils.getBooleanFromWeightedChance(upgrade.getMultiplier(level), Main.random)) {
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    Main.getEconomy().depositPlayer(p, upgrade.getValue(level));
                                }
                            }
                            break;
                        case "MONEYFINDER":
                            if (MiscUtils.getBooleanFromWeightedChance(upgrade.getMultiplier(level), Main.random)) {
                                Main.getEconomy().depositPlayer(player, upgrade.getValue(level));
                            }
                            break;
                        case "ERUPTION":
                            if (MiscUtils.getBooleanFromWeightedChance(upgrade.getMultiplier(level), Main.random)) {
                                for (UUID e : MiscUtils.getPlayersInRange(player, upgrade.getValue(level).intValue())) {
                                    Player p = Bukkit.getPlayer(e);
                                    for (String name : upgrade.getFunction()) {
                                        p.addPotionEffect(new PotionEffect(PotionEffectType.getByName(name), upgrade.getValue(level).intValue() * 20, level - 1));
                                    }
                                }
                            }
                            break;
                        case "EXPLOSION":
                            if (MiscUtils.getBooleanFromWeightedChance(upgrade.getMultiplier(level), Main.random)) {
                                TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(block.getLocation(), EntityType.PRIMED_TNT);
                                tnt.setYield(4);
                                tnt.setIsIncendiary(false);
                                tnt.setCustomName(player.getName());
                                OreBreakTaskType.getTntPrimedList().add(tnt);
                            }
                            break;
                        case "FORTUNE":
                            fortune = level;
                            break;
                        case "TSUNAMI":
                        default:
                            break;
                    }
                }
            }

            if (blockList == null || blockList.isEmpty()) {
                blockList = Arrays.asList(new Block[]{block});
            }

            int count = 0;
            List<ItemStack> dropsList = new ArrayList<>();
            for (Block b : blockList) {
                if (MiscUtils.getOres().contains(b.getType())) {
                    ItemStack loot = MiscUtils.getOreDrops(b.getType(), smelt, fortune);
                    if (autoSell && Main.getInstance().hasShopGUIPlus()) {
                        double cost = ShopGuiPlusApi.getItemStackPriceSell(loot);
                        if (cost > 0) {
                            totalSold += cost;
                        }
                        Main.getEconomy().depositPlayer(player, cost);
                    } else {
                        if (loot != null) {
                            dropsList.add(loot.clone());
                        }
                    }
                    if (experience > 0) player.giveExp(experience);
                    if (Main.getInstance().hasOreRegenerator()) regenBlock(b, 5, Material.STONE);
                    count++;
                }
            }

            DropPickaxeLootEvent dropPickaxeLootEvent = new DropPickaxeLootEvent(false, dropsList, player);
            Main.getInstance().getServer().getPluginManager().callEvent(dropPickaxeLootEvent);

            player.setItemInHand(tool.getItem(multi, value + count, totalSold, pickaxe));
            TokensReceiveEvent tokensReceiveEvent = new TokensReceiveEvent(profile, amount * count, tool.getTokenName());
            Main.getInstance().getServer().getPluginManager().callEvent(tokensReceiveEvent);
        }

        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock() == null || event.getPlayer() == null || event.getPlayer().isDead() || !MiscUtils.getOres().contains(event.getBlock().getType()))
            return;
        Player player = event.getPlayer();
        List<Block> blockList = Arrays.asList(new Block[]{event.getBlock()});
        if (mine(player, blockList))
            event.setCancelled(true);
    }

    private void regenBlock(Block b, int time, Material replace) {
        RegenUtils.doRegen(b.getLocation(), b.getType(), time);
        b.setType(replace);
    }
}
