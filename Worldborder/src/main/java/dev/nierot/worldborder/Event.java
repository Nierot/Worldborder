package dev.nierot.worldborder;

import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.*;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import dev.nierot.worldborder.Border;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
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
        // Player player = event.getPlayer();
        // Bukkit.broadcastMessage("Dankzij " + player.getName() + " is de worldborder 1
        // block groter!");
        // app.incrementSize(1, 100);
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
        app.decrementTime(app.time_multiplier);
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
    public void onQuit(PlayerQuitEvent event) {
        Bukkit.broadcastMessage(event.getPlayer().getDisplayName() + " is een mietje");
    }

    @EventHandler
    public void onFishing(PlayerFishEvent event) {
        if (event != null) {
            String name = event.getCaught().getName();
            String player_name = event.getPlayer().getName();
            if (name != null) {
                switch (name) {
                    case "Name Tag":
                        app.sendBroadcast(
                                player_name + " heeft een name tag gevangen! Hierdoor gaat de border sneller!");
                        app.decrementTime(50);
                        break;
                    case "Pufferfish":
                        app.sendBroadcast(player_name
                                + " heeft een pufferfish gevangen! Dat vind ik maar niks en de border gaat nu langzamer.");
                        app.decrementTime(-10);
                        break;
                    case "Water Bottle":
                        app.sendBroadcast("OMG " + player_name
                                + " heeft fucking monster energy gevangen!!!!!!!!!!!!!! Nu gaat alles sneller!!");
                        app.decrementTime(50);
                        break;
                    case "Bow":
                        app.sendBroadcast(player_name + " heeft een boog gevangen! Power 4 toch?");
                        app.decrementTime(20);
                        break;
                    case "Leather Boots":
                        app.sendBroadcast(player_name + " heeft nieuwe pattas");
                        app.decrementTime(20);
                        break;
                    case "Fishing Rod":
                        app.sendBroadcast(player_name + " heeft een fishing rod gevangen Xd");
                        app.decrementTime(10);
                        break;
                    case "Rotten Flesh":
                        app.sendBroadcast(player_name + " heeft lekker eten gevange xxxxdddd");
                        app.decrementTime(-40);
                        break;
                    case "Enchanted Book":
                        // Map<Enchantment, Integer> enchants = ((EnchantmentStorageMeta) ((Item) event.getCaught()).getItemStack().getItemMeta()).getEnchants();
                        // //app.sendBroadcast(((Item) event.getCaught()).getItemStack().getItemMeta().getAttributeModifiers().toString());
                        // app.sendBroadcast(Integer.toString(enchants.size()));
                        // StringBuilder sb = new StringBuilder();
                        // for (Map.Entry<Enchantment, Integer> enchant : enchants.entrySet()) {
                        //     String thisEnchant = enchant.getKey().getKey().getNamespace() + " " + enchant.getValue().toString();
                        //     app.sendBroadcast(enchant.getKey().getKey().toString());
                        //     app.sendBroadcast(enchant.getValue().toString());
                        //     sb.append(thisEnchant + ", ");
                        // }
                        app.sendBroadcast(player_name + " heeft een Enchanted Book gevangen!");
                        app.decrementTime(100);
                        break;
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
