package dev.nierot.worldborder;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.*;
import org.bukkit.Bukkit;

public final class Event implements Listener {

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Bukkit.broadcastMessage(event.getPlayer().getName() + " is verslaafd aan minecraft");
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        String player = event.getPlayer().getName();
        Bukkit.broadcastMessage("Dankzij " + player + " is de worldborder 1 block groter!");
        Bukkit.getWorld("world").getWorldBorder().setSize(Bukkit.getWorld("world").getWorldBorder().getSize() + 1, 60);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        String player = event.getPlayer().getName();
        Bukkit.broadcastMessage("Dankzij " + player + " is de worldborder 1 block kleiner! Kutjoch");
        Bukkit.getWorld("world").getWorldBorder().setSize(Bukkit.getWorld("world").getWorldBorder().getSize() - 1, 10);
    }
}
