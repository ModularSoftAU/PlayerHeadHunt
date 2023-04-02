package com.modularenigma.EasterEggHunt;

import com.modularenigma.EasterEggHunt.commands.cleareggs;
import com.modularenigma.EasterEggHunt.commands.eggs;
import com.modularenigma.EasterEggHunt.commands.counteggs;
import com.modularenigma.EasterEggHunt.commands.leaderboardeggs;
import com.modularenigma.EasterEggHunt.events.EggFindEvent;
import com.modularenigma.EasterEggHunt.events.EggHatOnHead;
import com.modularenigma.EasterEggHunt.events.EggHunterOnJoin;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class EasterEggHuntMain extends JavaPlugin {
    private PluginConfig config;
    private Connection connection;
    private ConsoleCommandSender console;

    public PluginConfig config() {
        return config;
    }

    @Override
    public void onEnable() {
        // Generate configuration file
        // plugin.saveDefaultConfig();
        config = new PluginConfig(this);
        console = getServer().getConsoleSender();

        EggChatController eggChatController = new EggChatController(this);
        EggWorldController eggWorldController = new EggWorldController(this);
        EggHatController eggHatController = new EggHatController(this);
        EggScoreboardController eggScoreboardController = new EggScoreboardController(this);

        // Connect to the database
        establishConnection();

        // Do an initial calculation of the number of eggs. This can be
        // manually recalculated with the relevant command.
        eggWorldController.countEggsInRegion();

        // Plugin Event Register
        PluginManager pluginmanager = getServer().getPluginManager();
        pluginmanager.registerEvents(new EggFindEvent(this, eggWorldController, eggChatController, eggHatController, eggScoreboardController), this);
        pluginmanager.registerEvents(new EggHunterOnJoin(this, eggChatController, eggScoreboardController), this);
        pluginmanager.registerEvents(new EggHatOnHead(), this);

        // Command Registry
        Objects.requireNonNull(getCommand("eggs")).setExecutor(new eggs(this, eggChatController));
        Objects.requireNonNull(getCommand("cleareggs")).setExecutor(new cleareggs(this, eggChatController, eggHatController, eggScoreboardController));
        Objects.requireNonNull(getCommand("counteggs")).setExecutor(new counteggs(this, eggWorldController, eggScoreboardController));
//        Objects.requireNonNull(getCommand("leaderboardeggs")).setExecutor(new leaderboardeggs(this, eggChatController));

        // Plugin Load Message
        console.sendMessage(ChatColor.GREEN + getDescription().getName() + " is now enabled.");
        console.sendMessage(ChatColor.GREEN + "Running Version: " + getDescription().getVersion());
        console.sendMessage(ChatColor.GREEN + "GitHub Repository: https://github.com/craftingforchrist/EasterEggHunt");
        console.sendMessage(ChatColor.GREEN + "Created By: " + getDescription().getAuthors());
    }

    @Override
    public void onDisable() {
        // Plugin Shutdown Message
        console.sendMessage(ChatColor.RED + getDescription().getName() + " is now disabled.");
    }

    public void establishConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setServerName(config.getDatabaseHost());
            dataSource.setPort(config.getDatabasePort());
            dataSource.setDatabaseName(config.getDatabaseName());
            dataSource.setUser(config.getDatabaseUsername());
            dataSource.setPassword(config.getDatabasePassword());
            connection = dataSource.getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            getLogger().info(config.getLangDatabaseConnectionError());
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                getLogger().info(config.getLangDatabaseConnectionError());
                e.printStackTrace();
            }
        }
        establishConnection();
        return connection;
    }
}
