package dev.nierot.worldborder;

import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.*;

import dev.nierot.worldborder.Border;

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
    public void onBorderGrow(Border event) {
        Bukkit.broadcastMessage("Border event!")
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Bukkit.broadcastMessage(event.getPlayer().getName() + " is verslaafd aan minecraft");
        if (Bukkit.getServer().getOnlinePlayers().size() == 1) {
            app.startBorderChecker();
        }
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        //Player player = event.getPlayer();
        //Bukkit.broadcastMessage("Dankzij " + player.getName() + " is de worldborder 1 block groter!");
        //app.incrementSize(1, 100);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        String player = event.getPlayer().getName();
        int size = app.getSize();
        Bukkit.broadcastMessage("--------------------------------------");
        Bukkit.broadcastMessage("De worldborder is nu " + size + " groot!");
        Bukkit.broadcastMessage("Echter wordt hij dankzij " + player + " " + (size - 1) + " groot");
        Bukkit.broadcastMessage("Allemaal even bedankt zeggen voor deze top actie!");
        Bukkit.broadcastMessage("--------------------------------------");
        app.decrementBorder(1);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        int players = Bukkit.getServer().getOnlinePlayers().size();
        app.getLogger().info("yeet" + players);
        if (players == 1) {
            app.getLogger().info("No players remaining... Stopping border checking thread.");
            app.stopBorderCheckers(app.taskID);
        }
    }

    @EventHandler
    public void onJeffreyDiamond(BlockBreakEvent event) {
        if (event.getBlock().getType().toString().equals("DIAMOND_ORE")) {
            if (event.getPlayer().getName().equals("Ruzlier")) {
                app.sendBroadcast("JEFFREY HEEFT EEN DIAMOND GEVONDEN");
                app.sendBroadcast("Hierdoor gaat de border sneller!");
                app.decrementTime(20);
            } else {
                app.sendBroadcast(event.getPlayer().getName() + " vond zojuist DIAMONDS!!!11!!");
            }
        }
    }

    @EventHandler
    public void onFishing(PlayerFishEvent event) {
        if (event != null) {
            String name = event.getCaught().getName();
            String player_name = event.getPlayer().getName();
            if (name != null) {
                switch(name) {
                    case "Name Tag":
                        app.sendBroadcast(player_name + " heeft een name tag gevangen! Hierdoor gaat de border sneller!");
                        app.decrementTime(50);
                        break;
                    case "Pufferfish":
                        app.sendBroadcast(player_name + " heeft een pufferfish gevangen! Dat vind ik maar niks en de border gaat nu langzamer.");
                        app.decrementTime(-10);
                        break;
                    case "Water Bottle":
                        app.sendBroadcast("OMG " + player_name + " heeft fucking monster energy gevangen!!!!!!!!!!!!!! Nu gaat alles sneller!!");
                        app.decrementTime(50);
                    default:
                        if (name.equalsIgnoreCase("Raw Cod") || name.equalsIgnoreCase("Raw Salmon") || name.equalsIgnoreCase("Tropical Fish")) {
                            app.caughtFish(player_name);
                        }
                        break;
                }
            }
        }
    }
}
