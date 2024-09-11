package net.hylustpickaxes.src;

import net.hylustpickaxes.src.commands.MoonlightPickaxes;
import net.hylustpickaxes.src.commands.Tokens;
import net.hylustpickaxes.src.commands.UpgradeCmd;
import net.hylustpickaxes.src.config.ConfigData;
import net.hylustpickaxes.src.config.ConfigHandler;
import net.hylustpickaxes.src.listener.InventoryListener;
import net.hylustpickaxes.src.listener.MiningListener;
import net.hylustpickaxes.src.listener.PlayerListener;
import net.hylustpickaxes.src.listener.CustomEventListener;
import net.hylustpickaxes.src.profiles.Profile;
import net.hylustpickaxes.src.profiles.ProfileManager;
import net.hylustpickaxes.src.shop.ShopManager;
import net.hylustpickaxes.src.tools.ToolManger;
import net.hylustpickaxes.src.upgrades.UpgradeManager;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Main extends JavaPlugin {

    public static Main plugin;
    public static MiniMessage mm = MiniMessage.miniMessage();
    private static UpgradeManager upgradeManager;
    private static ToolManger toolManger;
    private static ProfileManager profileManager;
    private static ConfigHandler configHandler;
    private static ShopManager shopManager;
    public static Random random;
    public HashMap<UUID, Integer> grenadeCDTime = new HashMap<UUID, Integer>();
    public static List<Fireball> fireballs = new ArrayList<>();
    public static List<TNTPrimed> tntPrimedList = new ArrayList<>();

    public void onEnable() {
        plugin = this;
        random = new Random();

        saveDefaultConfig();

        upgradeManager = new UpgradeManager();
        toolManger = new ToolManger();
        profileManager = new ProfileManager();
        configHandler = new ConfigHandler(this);
        shopManager = new ShopManager(this);

        loadCommands();
        loadListeners();
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
        
        for (Player player : Bukkit.getOnlinePlayers()) {
	    	Profile profile = new Profile(player.getUniqueId());
	        if (Main.getProfileManager().getProfile(player) == null) {
	            Main.getProfileManager().getProfiles().add(profile);
	        }
    	}
    }

    public void onDisable() {
        profileManager.saveData();
        upgradeManager = null;
        toolManger = null;
        configHandler = null;
        shopManager = null;
        for (TNTPrimed tnt : tntPrimedList)
        {
            tnt.remove();
        }
        for (Fireball fb : fireballs)
        {
            fb.remove();
        }
    }

    public static Main getInstance() {
        return plugin;
    }

    public void loadCommands() {
        getServer().getPluginCommand("moonlightpickaxes").setExecutor(new MoonlightPickaxes());
        if (ConfigData.accessUpgradeMenu.equalsIgnoreCase("both") || ConfigData.accessUpgradeMenu.equalsIgnoreCase("command")) getServer().getPluginCommand("upgrade").setExecutor(new UpgradeCmd());
        getServer().getPluginCommand("tokens").setExecutor(new Tokens());
    }

    public void loadListeners() {
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CustomEventListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new MiningListener(), this);
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

    public static List<Fireball> getFireballs() {
        return fireballs;
    }

    public static List<TNTPrimed> getTntPrimedList() {
        return tntPrimedList;
    }

}
