package net.craftingforchrist.EasterEggHunt;

import net.craftingforchrist.EasterEggHunt.commands.egg;
import net.craftingforchrist.EasterEggHunt.events.EggFindEvent;
import net.craftingforchrist.EasterEggHunt.events.EggHunterOnJoin;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class EasterEggHuntMain extends JavaPlugin {
    public static EasterEggHuntMain plugin;
    private Connection connection;

    @Override
    public void onEnable() {
        plugin = this;

        establishConnection(); // Connect to the database

        // Plugin Load Message
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "\n\n" + plugin.getDescription().getName() + " is now enabled.\nRunning Version: " + plugin.getDescription().getVersion() + "\nGitHub Repository: https://github.com/craftingforchrist/EasterEggHunt\nCreated By: " + plugin.getDescription().getAuthors() + "\n\n");

        // Plugin Event Register
        PluginManager pluginmanager = plugin.getServer().getPluginManager();
        pluginmanager.registerEvents(new EggFindEvent(this), this);
        pluginmanager.registerEvents(new EggHunterOnJoin(this), this);

        // Command Registry
        this.getCommand("egg").setExecutor(new egg(this));

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
            plugin.connection = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("database.host") + ":" + plugin.getConfig().getString("database.port") + "/" + plugin.getConfig().getString("database.database"), plugin.getConfig().getString("database.username"), plugin.getConfig().getString("database.password"));
            plugin.getLogger().info(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', net.md_5.bungee.api.ChatColor.GREEN + " Database connection was successful."));
        } catch (SQLException e) {
            plugin.getLogger().info(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', net.md_5.bungee.api.ChatColor.RED + " Database connection failed!"));
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            plugin.getLogger().info(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', net.md_5.bungee.api.ChatColor.RED + " Database connection failed!"));
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
                plugin.getLogger().info(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', net.md_5.bungee.api.ChatColor.RED + " Database connection failed!"));
                e.printStackTrace();
            }
            establishConnection();
        }
        return connection;
    }

}
