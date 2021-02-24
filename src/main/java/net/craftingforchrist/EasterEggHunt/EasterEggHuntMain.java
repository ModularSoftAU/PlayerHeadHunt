package net.craftingforchrist.EasterEggHunt;

import net.craftingforchrist.EasterEggHunt.events.EggFindEvent;
import net.craftingforchrist.EasterEggHunt.events.EggHunterOnJoin;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class EasterEggHuntMain extends JavaPlugin {
    private static EasterEggHuntMain plugin;
    private Connection connection;

    @Override
    public void onEnable() {
        plugin = this;

        establishConnection(); // Connect to the database

        // Plugin Load Message
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "\n\n" + plugin.getDescription().getName() + " is now enabled.\nRunning Version: " + plugin.getDescription().getVersion() + "\nGitHub Repository: https://github.com/craftingforchrist/EasterEggHunt\nCreated By: " + plugin.getDescription().getAuthors() + "\n\n");

        // Plugin Event Register
        PluginManager pluginmanager = this.getServer().getPluginManager();
        pluginmanager.registerEvents(new EggFindEvent(), this);
        pluginmanager.registerEvents(new EggHunterOnJoin(this), this);

        plugin.saveDefaultConfig(); // Generate configuration file
    }

    @Override
    public void onDisable() {
        // Plugin Shutdown Message
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "\n\n" + plugin.getDescription().getName() + " is now disabled.\n\n");
    }

    public void establishConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("database.host") + ":" + plugin.getConfig().getString("database.port") + "/" + plugin.getConfig().getString("database.database"), plugin.getConfig().getString("database.username"), plugin.getConfig().getString("database.password"));
            this.getLogger().info(ChatColor.translateAlternateColorCodes('&', ChatColor.GREEN + " Database connection was successful."));
        } catch (SQLException e) {
            this.getLogger().info(ChatColor.translateAlternateColorCodes('&', ChatColor.RED + " Database connection failed!"));
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            this.getLogger().info(ChatColor.translateAlternateColorCodes('&', ChatColor.RED + " Database connection failed!"));
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        if (this.connection == null) {
            establishConnection();
        } else {
            try {
                this.connection.close();
            } catch (SQLException e) {
                this.getLogger().info(ChatColor.translateAlternateColorCodes('&', ChatColor.RED + " Database connection failed!"));
                e.printStackTrace();
            }
            establishConnection();
        }
        return connection;
    }

}
