package dev.nierot.worldborder;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.WorldBorder;

public class App extends JavaPlugin {

    private Connection connection;
    private Statement statement;
    private String host, database, username, password, port;
    private int size;
    private File file;
    private FileWriter writer;
    

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Event(this), this);
        host = db.host;
        database = db.database;
        username = db.username;
        password = db.password;
        port = Integer.toString(db.port);
        try {    
            openConnection();
            statement = connection.createStatement();          
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            this.size = getSize();
        } catch (Exception e) {
            Bukkit.broadcastMessage("MariaDB doet nait");
        }
    }

    public Statement getStatement() {
        return this.statement;
    }

    public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }
     
        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database, this.username, this.password);
        }
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
                    updateSize(Integer.parseInt(args[1]));
                } else if (args.length == 3) {
                    border.setSize(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                    sender.sendMessage("Set the worldborder to size: " + args[1]);
                    updateSize(Integer.parseInt(args[1]));
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
                    updateSize(Integer.parseInt(args[1]),1);
                    return true;
                }
            } else if (args[0].equals("dbtest")) {
                dbTest();
                return true;
            }
        }
        return false;
    }

    public void dbTest() {
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM mc.border");
            while (result.next()) {
                String name = result.getString("size");
                Bukkit.broadcastMessage(name);
            }
        } catch (SQLException e) {
            Bukkit.broadcastMessage(e.toString());
        } catch (Exception f) {
            Bukkit.broadcastMessage("Something went wrong");
        }
    }

    public int getSize() {
        try {
            ResultSet res = statement.executeQuery("SELECT size FROM mc.size");
            int size = Integer.parseInt(res.getString("size"));
            this.size = size;
        } catch (SQLException e) {
            Bukkit.broadcastMessage(e.toString());
        } catch (Exception f) {
            Bukkit.broadcastMessage("Something went wrong");
        }
        return this.size;
    }

    public void incrementSize(int inc, int time) {
        this.size += inc;
        updateSize(this.size, time);
    }

    public void updateSize(int size, int time) {
        this.size = size;
        Bukkit.getWorld("world").getWorldBorder().setSize(size, time);
        try {
            statement.executeUpdate("UPDATE mc.size SET size=" + size);
        } catch (SQLException e) {
            Bukkit.broadcastMessage(e.toString());
        } catch (Exception f) {
            Bukkit.broadcastMessage("Something went wrong");
        }
    }
}
