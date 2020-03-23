package dev.nierot.worldborder;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.WorldBorder;

public class App extends JavaPlugin {

    private Connection connection;
    private Statement statement;
    private String host, database, username, password, port;
    private int size, time;
    public int currentSize, taskID;

    public int time_multiplier = 40;

    public void sendBroadcast(String text) {
        Bukkit.broadcastMessage(ChatColor.RED + "[Worldborder] " + ChatColor.WHITE + text);
    }

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
            getSize();
            getTime();
            this.currentSize = this.size;
        } catch (Exception e) {
            sendBroadcast("MariaDB doet nait");
        }
        this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
            
            @Override
            public void run() {
                checkBorder();
            }
        }, 0L, 400L);
    }

    public void checkBorder() {
        int size = getSize();
        double currentSize = Bukkit.getWorld("world").getWorldBorder().getSize();
        getLogger().info("Checking worldborder size...");
        if (currentSize == size) {
            sendBroadcast("De border is nu " + this.size + " blokken groot! Volgende blok in " + this.time + " seconden!");
            incrementBorder(1);
        }
    }

    public void startBorderChecker() {
        getLogger().info("Starting border checker thread...");
        if (this.taskID != 0) {
            this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
                
                @Override
                public void run() {
                    checkBorder();
                }
            }, 0L, 400L);
        } else {
            Bukkit.getServer().getScheduler().cancelTask(this.taskID);
            startBorderChecker();
        }
    }

    public void stopBorderCheckers(int taskID) {
        getLogger().info("Stopping border checker thread.");
        Bukkit.getServer().getScheduler().cancelTask(taskID);
    }

    public void incrementBorder(int size) {
        incrementTime();
        incrementSize(size);
        getWorldBorder().setSize(this.getSize(), this.getTime());
    }

    public void decrementBorder(int size) {
        incrementSize(-1*size);
        getWorldBorder().setSize(this.getSize(), 1);
    }

    public WorldBorder getWorldBorder() {
        return Bukkit.getWorld("world").getWorldBorder();
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
                player.sendMessage(player.getDisplayName() + " lmao");
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
                    sendBroadcast("Set the worldborder to size: " + args[1]);
                    setSize(Integer.parseInt(args[1]));
                    setTime(Integer.parseInt(args[1]));
                    getWorldBorder().setSize(Integer.parseInt(args[1]));
                } else if (args.length == 3) {
                    border.setSize(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                    sender.sendMessage("Set the worldborder to size: " + args[1]);
                    setSize(Integer.parseInt(args[1]));
                    setTime(Integer.parseInt(args[1]));
                    getWorldBorder().setSize(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                }
            return true;
            } else if (args[0].equals("getsize")) {
                sender.sendMessage(Integer.toString(getSize()));
                return true;
            } else if (args[0].equals("gettime")) {
                sender.sendMessage(Integer.toString(getTime()));
                return true;
            } else if (args[0].equals("new")) {
                sendBroadcast("Creating a new border");
                if (args.length != 4) {
                    sender.sendMessage("Usage: /wb new x z size");
                    return true;
                } else {
                    border.setCenter(Double.parseDouble(args[1]),Double.parseDouble(args[2]));
                    border.setSize(Double.parseDouble(args[3]));
                    sendBroadcast("A new border is created at coordinates: " + args[1] + ", " + args[2] + " with size: " + args[3]);
                    setSize(Integer.parseInt(args[3]));
                    setTime(Integer.parseInt(args[3]));
                    return true;
                }
            } else if (args[0].equals("dbtest")) {
                dbTest();
                Border event = new Border("Yeet");
                Bukkit.getServer().getPluginManager().callEvent(event);
                return true;
            }
        }
        return false;
    }

    public void dbTest() {
        try {
            sendBroadcast("SELECT * FROM " + db.database +".border");
            ResultSet result = statement.executeQuery("SELECT * FROM " + db.database +".border");
            while (result.next()) {
                String name = result.getString("size");
                sendBroadcast(name);
            }
        } catch (SQLException e) {
            sendBroadcast(e.toString());
        } catch (Exception f) {
            sendBroadcast("Something went wrong");
        }
    }

    public int getSize() {
        try {
            ResultSet res = statement.executeQuery("SELECT size FROM " + db.database + ".size");
            if (res.next()) {
                int size = Integer.parseInt(res.getString("size"));
                this.size = size;
            }
        } catch (SQLException e) {
            Bukkit.broadcastMessage("Reee");
        }
        return this.size;
    }

    public int getTime() {
        try {
            ResultSet res = statement.executeQuery("SELECT time FROM " + db.database + ".time");
            if (res.next()) {
                int time = Integer.parseInt(res.getString("time"));
                this.time = time;
            }
        } catch (SQLException e) {
            sendBroadcast(e.toString());
        } catch (Exception f) {
            sendBroadcast(f.toString());
        }
        return this.time;
    }

    public void incrementSize(int i) {
        try {
            int size = this.size + i;
            statement.executeUpdate("UPDATE " + db.database + ".size SET size=" + size);
            this.size += 1;
        } catch (SQLException e) {
            sendBroadcast(e.toString());
        } catch (Exception f) {
            sendBroadcast("Something went wrong");
        }
    }

    public void setSize(int i) {
        try {
            this.size = i;
            statement.executeUpdate("UPDATE " + db.database + ".size SET size=" + this.size);
        } catch (SQLException e) {
            sendBroadcast(e.toString());
        } catch (Exception f) {
            sendBroadcast("Something went wrong");
        }
    }

    public void setTime(int size) {
        try {
            this.time = size * time_multiplier;
            statement.executeUpdate("UPDATE " + db.database + ".time SET time=" + this.time);
        } catch (SQLException e) {
            sendBroadcast(e.toString());
        } catch (Exception f) {
            sendBroadcast("Something went wrong");
        }
    }

    public void decrementTime(int time) {
        try {
            this.time -= time;
            statement.executeUpdate("UPDATE " + db.database + ".time SET time=" + this.time);
        } catch (Exception e) {
            sendBroadcast(e.toString());
        }
    }

    public void incrementTime() {
        try {
            int time = this.time + time_multiplier;
            statement.executeUpdate("UPDATE " + db.database + ".time SET time=" + time);
            this.time += 100;
        } catch (Exception e) {
            sendBroadcast(e.toString());
        }
    }

    public void caughtFish(String player) {
        try {
            ResultSet res = statement.executeQuery("SELECT * FROM " + db.database + ".fish WHERE player='" + player + "'");
            if (res.next()) {
                statement.executeUpdate("UPDATE " + db.database + ".fish SET fish = fish + 1 WHERE player='" + player + "'");
            } else {
                statement.executeUpdate("INSERT INTO " + db.database + ".fish VALUES(1)");
            }
        } catch (Exception e) {
            sendBroadcast(e.toString());
        }
    }
}
