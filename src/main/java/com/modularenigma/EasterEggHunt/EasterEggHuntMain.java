package com.modularenigma.EasterEggHunt;

import com.modularenigma.EasterEggHunt.commands.cleareggs;
import com.modularenigma.EasterEggHunt.commands.eggs;
import com.modularenigma.EasterEggHunt.events.EggFindEvent;
import com.modularenigma.EasterEggHunt.events.EggHatOnHead;
import com.modularenigma.EasterEggHunt.events.EggHunterOnJoin;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class EasterEggHuntMain extends JavaPlugin {
    public static EasterEggHuntMain plugin;
    private Connection connection;

//    public Variables Variables;
    public EggController EggController;
    public EggChatController EggChatController;

    @Override
    public void onEnable() {
        plugin = this;
        plugin.saveDefaultConfig(); // Generate configuration file

//        Variables Variables = new Variables(this);
        EggController EggController = new EggController(this);
        EggChatController EggChatController = new EggChatController(this);
        EggScoreboardController EggScoreboardController = new EggScoreboardController(this);
        EggHatController EggHatController = new EggHatController(this);

        establishConnection(); // Connect to the database
        EggController.setTotalEggBlocks();

        // Plugin Load Message
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "\n\n" + plugin.getDescription().getName() + " is now enabled.\nRunning Version: " + plugin.getDescription().getVersion() + "\nGitHub Repository: https://github.com/craftingforchrist/EasterEggHunt\nCreated By: " + plugin.getDescription().getAuthors() + "\n\n");

        // Plugin Event Register
        PluginManager pluginmanager = plugin.getServer().getPluginManager();
        pluginmanager.registerEvents(new EggFindEvent(this), this);
        pluginmanager.registerEvents(new EggHunterOnJoin(this), this);
        pluginmanager.registerEvents(new EggHatOnHead(this), this);

        // Command Registry
        this.getCommand("eggs").setExecutor(new eggs(this));
        this.getCommand("cleareggs").setExecutor(new cleareggs(this));
    }

    @Override
    public void onDisable() {
        // Plugin Shutdown Message
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "\n\n" + plugin.getDescription().getName() + " is now disabled.\n\n");
    }

    public void establishConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            plugin.connection = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("DATABASE.HOST") + ":" + plugin.getConfig().getString("DATABASE.PORT") + "/" + plugin.getConfig().getString("DATABASE.DATABASE"), plugin.getConfig().getString("DATABASE.USERNAME"), plugin.getConfig().getString("DATABASE.PASSWORD"));
            plugin.getLogger().info(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.DATABASE.CONNECTIONSUCCESS")));
        } catch (SQLException e) {
            plugin.getLogger().info(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.DATABASE.CONNECTIONERROR")));
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            plugin.getLogger().info(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.DATABASE.CONNECTIONERROR")));
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        if (plugin.connection == null) {
            establishConnection();
        } else {
            try {
                plugin.connection.close();
            } catch (SQLException e) {
                plugin.getLogger().info(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.DATABASE.CONNECTIONERROR")));
                e.printStackTrace();
            }
            establishConnection();
        }
        return connection;
    }
}
