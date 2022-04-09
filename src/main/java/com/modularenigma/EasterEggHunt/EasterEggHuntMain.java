package com.modularenigma.EasterEggHunt;

import com.modularenigma.EasterEggHunt.commands.cleareggs;
import com.modularenigma.EasterEggHunt.commands.eggs;
import com.modularenigma.EasterEggHunt.events.EggFindEvent;
import com.modularenigma.EasterEggHunt.events.EggHatOnHead;
import com.modularenigma.EasterEggHunt.events.EggHunterOnJoin;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class EasterEggHuntMain extends JavaPlugin {
    private static EasterEggHuntMain instance;
    private PluginConfig config;
    private Connection connection;
    private ConsoleCommandSender console;

    public static EasterEggHuntMain plugin() {
        assert instance != null;
        return instance;
    }

    public PluginConfig config() {
        return config;
    }

    @Override
    public void onEnable() {
        instance = this;
        config = new PluginConfig(this);
//        plugin.saveDefaultConfig(); // Generate configuration file
        console = getServer().getConsoleSender();

        establishConnection(); // Connect to the database
        EggController.instance().calculateTotalEggs();

        // Plugin Load Message
        console.sendMessage(ChatColor.GREEN + getDescription().getName() + " is now enabled.");
        console.sendMessage(ChatColor.GREEN + "Running Version: " + getDescription().getVersion());
        console.sendMessage(ChatColor.GREEN + "GitHub Repository: https://github.com/craftingforchrist/EasterEggHunt");
        console.sendMessage(ChatColor.GREEN + "Created By: " + getDescription().getAuthors());

        // Plugin Event Register
        PluginManager pluginmanager = getServer().getPluginManager();
        pluginmanager.registerEvents(new EggFindEvent(), this);
        pluginmanager.registerEvents(new EggHunterOnJoin(), this);
        pluginmanager.registerEvents(new EggHatOnHead(), this);

        // Command Registry
        Objects.requireNonNull(getCommand("eggs")).setExecutor(new eggs());
        Objects.requireNonNull(getCommand("cleareggs")).setExecutor(new cleareggs());
    }

    @Override
    public void onDisable() {
        // Plugin Shutdown Message
        console.sendMessage(ChatColor.RED + getDescription().getName() + " is now disabled.");
    }

    public void establishConnection() {
        String host = config.getDatabaseHost();
        String port = config.getDatabasePort();
        String database = config.getDatabaseName();
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        String username = config.getDatabaseUsername();
        String password = config.getDatabasePassword();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            getLogger().info(config.getLangDatabaseConnectionSuccess());
        } catch (SQLException | ClassNotFoundException e) {
            getLogger().info(config.getLangDatabaseConnectionError());
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        if (connection == null) {
            establishConnection();
        } else {
            try {
                connection.close();
            } catch (SQLException e) {
                getLogger().info(config.getLangDatabaseConnectionError());
                e.printStackTrace();
            }
            establishConnection();
        }
        return connection;
    }
}
