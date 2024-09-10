package net.hylustpickaxes.src.tasktypes;

import com.leonardobishop.quests.api.QuestsAPI;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.ConfigValue;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import dev.mrshawn.oreregenerator.api.utils.RegenUtils;
import net.brcdev.shopgui.ShopGuiPlusApi;
import net.hylustpickaxes.src.Main;
import net.hylustpickaxes.src.config.ConfigData;
import net.hylustpickaxes.src.events.DropPickaxeLootEvent;
import net.hylustpickaxes.src.events.TokensReceiveEvent;
import net.hylustpickaxes.src.nbt.NBT;
import net.hylustpickaxes.src.profiles.Profile;
import net.hylustpickaxes.src.tools.Tool;
import net.hylustpickaxes.src.upgrades.Upgrade;
import net.hylustpickaxes.src.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class OreBreakTaskType extends TaskType {
    private static List<Fireball> fireballs = new ArrayList<>();
    private static List<TNTPrimed> tntPrimedList = new ArrayList<>();
    private List<ConfigValue> creatorConfigValues = new ArrayList();

    public OreBreakTaskType() {
        super("orebreakcertain", "DepressedChan", "Break a set amount of ore.");
        this.creatorConfigValues.add(new ConfigValue("amount", true, "Amount of blocks to be broken."));
        this.creatorConfigValues.add(new ConfigValue("block", false, "Name or ID of block."));
        this.creatorConfigValues.add(new ConfigValue("blocks", false, "List of blocks (alias for block for config readability)."));
        this.creatorConfigValues.add(new ConfigValue("data", false, "Data code for block."));
    }

    public static List<Fireball> getFireballs() {
        return fireballs;
    }

    public static List<TNTPrimed> getTntPrimedList() {
        return tntPrimedList;
    }

    public List<ConfigValue> getCreatorConfigValues() {
        return this.creatorConfigValues;
    }

    public boolean mine(Player player, List<Block> oldBlockList) {
        if (player.isDead() || player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR || NBT.get(player.getItemInHand()) == null)
            return false;
        ItemStack pickaxe = player.getItemInHand();
        if (Main.getToolManger().getTool(pickaxe) == null || !Main.getToolManger().itemIsTool(pickaxe) || !Main.getToolManger().isToolPickaxe(pickaxe))
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
                            if (upgrade.getValue(level) != 0)
                                experience += Main.random.nextInt(upgrade.getValue(level).intValue());
                            break;
                        case "AUTOSMELT":
                            if (level > 0)
                                smelt = true;
                            break;
                        case "AUTOSELL":
                            if (level > 0)
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
                                tntPrimedList.add(tnt);
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
                    if (Main.getInstance().hasShopGUIPlus() && autoSell) {
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

            questIncrement(player, count, block);
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

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (fireballs.contains(event.getEntity()))
            event.getEntity().remove();
    }

    @EventHandler
    public void onExplosionDamage(EntityDamageByEntityEvent event) {
        if (fireballs.contains(event.getDamager()) || tntPrimedList.contains(event.getDamager())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void grenadeExplode(EntityExplodeEvent event) {
        List<Block> blockList = event.blockList();
        Entity e = event.getEntity();
        Player player;
        if (e instanceof Fireball) {
            if (((Fireball) e).getShooter() instanceof Player) {
                player = (Player) ((Fireball) e).getShooter();
                event.setCancelled(true);
                mine(player, blockList);
            } else
                return;
        } else if (e instanceof TNTPrimed) {
            if (Bukkit.getPlayer(e.getCustomName()) != null) {
                player = Bukkit.getPlayer(e.getCustomName());
                event.setCancelled(true);
                mine(player, blockList);
            } else
                return;
        }
    }

    public void regenBlock(Block b, int time, Material replace) {
        RegenUtils.doRegen(b.getLocation(), b.getType(), time);
        b.setType(replace);
    }

    private void questIncrement(Player player, int count, Block block) {
        QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(player.getUniqueId(), true);
        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();

        for (Quest quest : getRegisteredQuests()) {
            if (questProgressFile.hasStartedQuest(quest)) {
                QuestProgress questProgress = questProgressFile.getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(getType())) {
                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    if (matchBlock(task, block)) {
                        increment(task, taskProgress, count);
                    }
                }
            }
        }
    }

    private boolean matchBlock(Task task, Block block) {
        Object configBlock = task.getConfigValues().containsKey("block") ? task.getConfigValue("block") : task.getConfigValue("blocks");
        Object configData = task.getConfigValue("data");

        List<String> checkBlocks = new ArrayList<String>();
        if (configBlock instanceof List) {
            checkBlocks.addAll((List) configBlock);
        } else {
            checkBlocks.add(String.valueOf(configBlock));
        }

        for (String materialName : checkBlocks) {

            String[] split = materialName.split(":");
            int comparableData = ((Integer) configData).intValue();
            if (split.length > 1) {
                comparableData = Integer.parseInt(split[1]);
            }

            Material material = Material.getMaterial(String.valueOf(split[0]));
            Material blockType = block.getType();

            short blockData = block.getData();

            if (blockType.equals(material)) {
                return (configData == null || blockData == comparableData);
            }
        }
        return false;
    }

    private void increment(Task task, TaskProgress taskProgress, int amount) {
        int progressBlocksBroken, brokenBlocksNeeded = ((Integer) task.getConfigValue("amount")).intValue();


        if (taskProgress.getProgress() == null) {
            progressBlocksBroken = 0;
        } else {
            progressBlocksBroken = ((Integer) taskProgress.getProgress()).intValue();
        }

        taskProgress.setProgress(Integer.valueOf(progressBlocksBroken + amount));

        if (((Integer) taskProgress.getProgress()).intValue() >= brokenBlocksNeeded)
            taskProgress.setCompleted(true);
    }
}
