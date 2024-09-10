package net.hylustpickaxes.src.events;

import net.hylustpickaxes.src.profiles.Profile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TokensReceiveEvent
        extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean isCancelled;

    private Profile profile;
    private int amount;
    private String type;

    public TokensReceiveEvent(Profile profile, int amount, String type) {
        this.profile = profile;
        this.amount = amount;
        this.type = type;
    }

    public static HandlerList getHandlerList() { return handlers; }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Profile getProfile() { return this.profile; }

    public int getAmount() { return this.amount; }

    public void setAmount(int amount) { this.amount = amount; }

    public HandlerList getHandlers() { return handlers; }

    public boolean isCancelled() { return this.isCancelled; }




    public void setCancelled(boolean isCancelled) { this.isCancelled = isCancelled; }
}
