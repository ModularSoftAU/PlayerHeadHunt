package com.modularenigma.EasterEggHunt;

import com.modularenigma.EasterEggHunt.commands.cleareggs;
import com.modularenigma.EasterEggHunt.commands.eggs;
import com.modularenigma.EasterEggHunt.events.EggFindEvent;
import com.modularenigma.EasterEggHunt.events.EggHatOnHead;
import com.modularenigma.EasterEggHunt.events.EggHunterOnJoin;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class EasterEggHuntMain extends JavaPlugin {
    private Connection connection;
    private String connectionSuccess;
    private String connectionError;

    @Override
    public void onEnable() {
//        plugin.saveDefaultConfig(); // Generate configuration file

        EggController.onEnable(this);
        EggChatController.onEnable(this);
        EggScoreboardController.onEnable(this);
        EggHatController.onEnable(this);

        String blankConnectionSuccess = getConfig().getString("LANG.DATABASE.CONNECTIONSUCCESS");
        String blankConnectionError = getConfig().getString("LANG.DATABASE.CONNECTIONERROR");
        assert blankConnectionSuccess != null;
        assert blankConnectionError != null;
        connectionSuccess = ChatColor.translateAlternateColorCodes('&', blankConnectionSuccess);
        connectionError = ChatColor.translateAlternateColorCodes('&', blankConnectionError);

        establishConnection(); // Connect to the database
        EggController.instance().calculateTotalEggs();

        // Plugin Load Message
        getServer().getConsoleSender().sendMessage(
                ChatColor.GREEN + "\n\n" +
                getDescription().getName() + " is now enabled.\n" +
                "Running Version: " + getDescription().getVersion() + "\n" +
                "GitHub Repository: https://github.com/craftingforchrist/EasterEggHunt\n" +
                "Created By: " + getDescription().getAuthors() + "\n\n");

        // Plugin Event Register
        PluginManager pluginmanager = getServer().getPluginManager();
        pluginmanager.registerEvents(new EggFindEvent(this), this);
        pluginmanager.registerEvents(new EggHunterOnJoin(this), this);
        pluginmanager.registerEvents(new EggHatOnHead(this), this);

        // Command Registry
        Objects.requireNonNull(getCommand("eggs")).setExecutor(new eggs(this));
        Objects.requireNonNull(getCommand("cleareggs")).setExecutor(new cleareggs(this));
    }

    @Override
    public void onDisable() {
        // Plugin Shutdown Message
        getServer().getConsoleSender().sendMessage(
                ChatColor.RED + "\n\n" +
                getDescription().getName() + " is now disabled.\n\n");
    }

    public void establishConnection() {
        String host = getConfig().getString("DATABASE.HOST");
        String port = getConfig().getString("DATABASE.PORT");
        String database = getConfig().getString("DATABASE.DATABASE");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
//        jdbc:mysql://127.0.0.1:3306/?user=jaedanc
        String username = getConfig().getString("DATABASE.USERNAME");
        String password = getConfig().getString("DATABASE.PASSWORD");

        try {
            Class.forName("com.mysql.jdbc.Driver");
//            MysqlDataSource source = new MysqlDataSource();
//            source.setServerName(host);
//            source.setPassword(password);
//            source.setDatabaseName(database);
//            source.setUser(username);
//            connection = source.getConnection();
            connection = DriverManager.getConnection(url, username, password);
            getLogger().info(connectionSuccess);
        } catch (SQLException | ClassNotFoundException e) {
            getLogger().info(connectionError);
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
                getLogger().info(connectionError);
                e.printStackTrace();
            }
            establishConnection();
        }
        return connection;
    }
}
