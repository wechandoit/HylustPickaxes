package net.hylustpickaxes.src.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DropPickaxeLootEvent
        extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean isCancelled;
    private List<ItemStack> drops;
    private Player player;

    public DropPickaxeLootEvent(boolean isCancelled, List<ItemStack> drops, Player player) {
        this.isCancelled = isCancelled;
        this.drops = drops;
        this.player = player;
    }

    public static HandlerList getHandlerList() { return handlers; }

    public List<ItemStack> getDrops() {
        return drops;
    }

    public void setDrops(List<ItemStack> drops) {
        this.drops = drops;
    }

    public Player getPlayer() {
        return player;
    }

    public HandlerList getHandlers() { return handlers; }

    public boolean isCancelled() { return this.isCancelled; }




    public void setCancelled(boolean isCancelled) { this.isCancelled = isCancelled; }
}
