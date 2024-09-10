package net.hylustpickaxes.src;

import com.leonardobishop.quests.api.QuestsAPI;
import com.leonardobishop.quests.quests.tasktypes.TaskTypeManager;
import net.hylustpickaxes.src.commands.MoonlightPickaxes;
import net.hylustpickaxes.src.commands.Tokens;
import net.hylustpickaxes.src.commands.UpgradeCmd;
import net.hylustpickaxes.src.config.ConfigHandler;
import net.hylustpickaxes.src.hooks.PlaceholderAPIHook;
import net.hylustpickaxes.src.listener.InventoryListener;
import net.hylustpickaxes.src.listener.MiningListener;
import net.hylustpickaxes.src.listener.PlayerListener;
import net.hylustpickaxes.src.listener.CustomEventListener;
import net.hylustpickaxes.src.profiles.ProfileManager;
import net.hylustpickaxes.src.shop.ShopManager;
import net.hylustpickaxes.src.tasktypes.OreBreakTaskType;
import net.hylustpickaxes.src.tools.ToolManger;
import net.hylustpickaxes.src.upgrades.UpgradeManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Main extends JavaPlugin {

    public static Main plugin;
    private static UpgradeManager upgradeManager;
    private static ToolManger toolManger;
    private static ProfileManager profileManager;
    private static ConfigHandler configHandler;
    private static ShopManager shopManager;
    public static Random random;
    private static Economy econ;
    public HashMap<UUID, Integer> grenadeCDTime = new HashMap<UUID, Integer>();
    private static List<Fireball> fireballs = new ArrayList<>();
    private static List<TNTPrimed> tntPrimedList = new ArrayList<>();

    public void onEnable() {
        plugin = this;

        if (!setupEconomy()) {
            Main.getInstance().getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        random = new Random();

        saveDefaultConfig();
        registerPlaceholders();

        upgradeManager = new UpgradeManager();
        toolManger = new ToolManger();
        profileManager = new ProfileManager();
        configHandler = new ConfigHandler(this);
        shopManager = new ShopManager(this);

        loadCommands();
        loadListeners();
        if (hasQuests())
            loadTaskTypes();

        grenadeRunner();
    }

    public void grenadeRunner() {
        new BukkitRunnable() {
            @Override
            public void run() {

                if (grenadeCDTime.isEmpty()) {
                    return;
                }

                for (UUID uuid : grenadeCDTime.keySet()) {
                    int timeleft = grenadeCDTime.get(uuid);
                    if (timeleft <= 0)
                        grenadeCDTime.remove(uuid);
                    else
                        grenadeCDTime.put(uuid, timeleft - 1);

                }
            }

        }.runTaskTimer(this, 0, 20);
    }

    public void reload() {
        upgradeManager = null;
        toolManger = null;
        profileManager = null;
        configHandler = null;
        shopManager = null;

        upgradeManager = new UpgradeManager();
        toolManger = new ToolManger();
        profileManager = new ProfileManager();
        configHandler = new ConfigHandler(this);
        shopManager = new ShopManager(this);
    }

    public void onDisable() {
        profileManager.saveData();
        upgradeManager = null;
        toolManger = null;
        configHandler = null;
        shopManager = null;
        for (TNTPrimed tnt : OreBreakTaskType.getTntPrimedList())
        {
            tnt.remove();
        }
        for (Fireball fb : OreBreakTaskType.getFireballs())
        {
            fb.remove();
        }
    }

    public static Main getInstance() {
        return plugin;
    }

    public void loadCommands() {
        getServer().getPluginCommand("moonlightpickaxes").setExecutor(new MoonlightPickaxes());
        getServer().getPluginCommand("upgrade").setExecutor(new UpgradeCmd());
        getServer().getPluginCommand("tokens").setExecutor(new Tokens());
    }

    public void loadListeners() {
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CustomEventListener(), this);
        if (!hasQuests()) Bukkit.getServer().getPluginManager().registerEvents(new MiningListener(), this);
    }

    public void loadTaskTypes() {
        TaskTypeManager taskTypeManager = QuestsAPI.getTaskTypeManager();
        taskTypeManager.registerTaskType(new OreBreakTaskType());
    }

    public static ProfileManager getProfileManager() {
        return profileManager;
    }

    public static ToolManger getToolManger() {
        return toolManger;
    }

    public static UpgradeManager getUpgradeManager() {
        return upgradeManager;
    }

    public static ShopManager getShopManager() { return shopManager; }

    private void registerPlaceholders() {
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPIHook(this).register();
            System.out.println("Placeholder registered!");
        }
    }

    public boolean hasShopGUIPlus() {
        return (getServer().getPluginManager().getPlugin("ShopGUIPlus") != null && getServer().getPluginManager().getPlugin("ShopGUIPlus").isEnabled());
    }

    public boolean hasQuests() {
        return (getServer().getPluginManager().getPlugin("Quests") != null && getServer().getPluginManager().getPlugin("Quests").isEnabled());
    }

    public boolean hasOreRegenerator() {
        return (getServer().getPluginManager().getPlugin("OreRegenerator") != null && getServer().getPluginManager().getPlugin("OreRegenerator").isEnabled());
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static List<Fireball> getFireballs() {
        return fireballs;
    }

    public static List<TNTPrimed> getTntPrimedList() {
        return tntPrimedList;
    }

    public static Economy getEconomy() {
        return econ;
    }


}
