package net.hylustpickaxes.src.listener;

import net.hylustpickaxes.src.Main;
import net.hylustpickaxes.src.events.DropPickaxeLootEvent;
import net.hylustpickaxes.src.events.TokensReceiveEvent;
import net.hylustpickaxes.src.profiles.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class CustomEventListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTokenRecieve(TokensReceiveEvent event) {
        if (event.isCancelled())
            return;
        Profile profile = event.getProfile();

        profile.addTokens(event.getAmount(), event.getType());
        Main.getProfileManager().saveData();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPickaxeLootDrop(DropPickaxeLootEvent event) {
        if (event.isCancelled())
            return;
        Player player = event.getPlayer();
        for (ItemStack itemStack : event.getDrops())
        {
            if (player.getInventory().firstEmpty() != -1)
            {
                player.getInventory().addItem(itemStack);
            } else
            {
                player.getWorld().dropItem(player.getLocation(), itemStack);
            }
        }
    }

}
