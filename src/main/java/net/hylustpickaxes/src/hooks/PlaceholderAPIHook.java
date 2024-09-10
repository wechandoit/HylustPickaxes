package net.hylustpickaxes.src.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.hylustpickaxes.src.Main;
import net.hylustpickaxes.src.profiles.Profile;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class PlaceholderAPIHook
        extends PlaceholderExpansion
{
    private Main instance;

    public PlaceholderAPIHook(Main instance) { this.instance = instance; }

    public boolean persist(){
        return true;
    }

    public boolean canRegister() { return true; }

    public String getIdentifier() { return "hylustpickaxes"; }

    public String getAuthor() { return this.instance.getDescription().getAuthors().toString(); }

    public String getVersion() { return this.instance.getDescription().getVersion(); }

    public String onPlaceholderRequest(Player player, String params) {
        Profile profile;
        if (player == null) return null;

        for (String tokenName : Main.getToolManger().getTokenTypes()) {

            if (("tokens_" + tokenName).equals(params)) {
                profile = Main.getProfileManager().getProfile(player);

                if (profile == null) return null;

                return String.valueOf(profile.getTokens(tokenName));
            } else if (("tokens_" + tokenName + "_formatted").equals(params)) {
                DecimalFormat df = new DecimalFormat("#,##0.00");
                profile = Main.getProfileManager().getProfile(player);

                if (profile == null) return null;

                return df.format(profile.getTokens(tokenName));
            }
        }


        return null;
    }
}

