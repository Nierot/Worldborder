package dev.nierot.worldborder;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.*;

import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class Event implements Listener {

    Statement statement;
    App app;

    public Event(App app) {
        this.app = app;
        this.statement = app.getStatement();
    }


    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Bukkit.broadcastMessage(event.getPlayer().getName() + " is verslaafd aan minecraft");
    }

    // @EventHandler
    // public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
    //     Player player = event.getPlayer();
    //     Bukkit.broadcastMessage("Dankzij " + player.getName() + " is de worldborder 1 block groter!");
    //     app.incrementSize(1, 100);
    // }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        String player = event.getPlayer().getName();
        Bukkit.broadcastMessage("Dankzij " + player + " is de worldborder 1 block kleiner! Kutjoch");
        app.incrementSize(-1, 10);
    }
}
