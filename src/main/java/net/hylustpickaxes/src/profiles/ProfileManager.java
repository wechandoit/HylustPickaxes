package net.hylustpickaxes.src.profiles;

import net.hylustpickaxes.src.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileManager {
    private List<Profile> profiles = new ArrayList<Profile>();
    private File file = new File("plugins/MoonlightPickaxes/", "data.yml");
    private FileConfiguration data = YamlConfiguration.loadConfiguration(file);

    public ProfileManager()
    {
        retrieveData();
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public void saveData() {

        data.set("profiles", new ArrayList<String>());
        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (profiles == null || profiles.isEmpty()) {
            return;
        }
        for (Profile profile : profiles) {
            for (String tokenName : Main.getToolManger().getTokenTypes()) {
                data.set("profiles." + profile.getUUID() + ".tokens." + tokenName, profile.getTokens(tokenName));
            }
        }
        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void retrieveData() {
        if (data.getConfigurationSection("profiles") == null) {
            return;
        }
        for (String s : data.getConfigurationSection("profiles").getKeys(false)) {
            Profile profile = new Profile(UUID.fromString(s));
            if (data.getConfigurationSection("profiles." + s + ".tokens") != null) {
                for (String token : data.getConfigurationSection("profiles." + s + ".tokens").getKeys(false)) {
                    int tokens = data.getInt("profiles." + s + ".tokens." + token);
                    profile.addTokens(tokens, token);
                }
            }
            profiles.add(profile);
        }
        Main.plugin.getLogger().info("Loaded " + profiles.size() + " profiles!");
    }

    public Profile getProfile(UUID uuid) {
        for (Profile profile : profiles) {
            if (profile.getUUID().equals(uuid)) return profile;
        }
        return null;
    }

    public Profile getProfile(Player player) {
        UUID uuid = player.getUniqueId();
        return getProfile(uuid);
    }


}
