package dev.nierot.worldborder;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class Border extends Event {

    public static final HandlerList handlers = new HandlerList();
    private String message;

    public Border(String msg) {
        message = msg;
    }

    public String getMessage() {
        return this.message;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}