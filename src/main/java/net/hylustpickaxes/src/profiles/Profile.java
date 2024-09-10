package net.hylustpickaxes.src.profiles;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Profile implements Comparable<Profile> {
    private UUID uuid;
    private HashMap<String, Integer> tokens = new HashMap<>();

    public Profile(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public int getTotalTokens()
    {
        int count = 0;
        if (tokens.isEmpty() || tokens == null) return 0;
        for(String type : tokens.keySet())
        {
            count += tokens.get(type);
        }
        return count;
    }

    public int getTokens(String tokenName)
    {
        if (tokens.isEmpty() || tokens == null || !tokens.containsKey(tokenName)) return 0;
        return tokens.get(tokenName);
    }

    public void addTokens(int amount, String type)
    {
        if (tokens == null) return;
        if (tokens.containsKey(type))
        {
            int previous = tokens.get(type);
            tokens.remove(type);
            tokens.put(type, previous + amount);
        } else
        {
            tokens.put(type, amount);
        }
    }

    public void removeTokens(int amount, String type)
    {
        if (tokens.isEmpty() || tokens == null) return;
        if (tokens.containsKey(type))
        {
            int previous = tokens.get(type);
            tokens.remove(type);
            tokens.put(type, previous - amount);
        } else
        {
            tokens.put(type, 0);
        }
    }

    public void setTokens(int amount, String type)
    {
        if (tokens == null) return;
        tokens.put(type, amount);

    }

    public boolean equals(Profile profile) {
        return (profile.getUUID().equals(uuid));
    }

    public int compareTo(Profile profile) {
        return getTotalTokens() - profile.getTotalTokens();
    }

    public String toString()
    {
        return uuid + "\n" + tokens;
    }
}
