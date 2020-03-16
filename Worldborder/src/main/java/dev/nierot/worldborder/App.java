package dev.nierot.worldborder;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.WorldBorder;

public class App extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Event(), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	    if (cmd.getName().equalsIgnoreCase("basic")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage("It works!");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("wb")) {
            WorldBorder border = Bukkit.getWorld("world").getWorldBorder();
            if (args.length == 0) {
                return false;
            } else if (args[0].equals("set")) {
                if (args.length == 1) {
                    return false;
                } else if (args.length == 2) {
                    border.setSize(Integer.parseInt(args[1]));
                    sender.sendMessage("Set the worldborder to size: " + args[1]);
                } else if (args.length == 3) {
                    border.setSize(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                    sender.sendMessage("Set the worldborder to size: " + args[1]);
                }
            return true;
            } else if (args[0].equals("getsize")) {
                sender.sendMessage(Double.toString(Bukkit.getWorld("world").getWorldBorder().getSize()));
                return true;
            } else if (args[0].equals("new")) {
                Bukkit.broadcastMessage("Creating a new border");
                if (args.length != 4) {
                    sender.sendMessage("Usage: /wb new x z size");
                    return true;
                } else {
                    border.setCenter(Double.parseDouble(args[1]),Double.parseDouble(args[2]));
                    border.setSize(Double.parseDouble(args[3]));
                    Bukkit.broadcastMessage("A new border is created at coordinates: " + args[1] + ", " + args[2] + " with size: " + args[3]);
                    return true;
                }
            }
        }
        return false;
    }
}