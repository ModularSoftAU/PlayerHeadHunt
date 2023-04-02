package org.modularsoft.PlayerHeadHunt;

import org.modularsoft.PlayerHeadHunt.commands.clearheads;
import org.modularsoft.PlayerHeadHunt.commands.heads;
import org.modularsoft.PlayerHeadHunt.commands.countheads;
import org.modularsoft.PlayerHeadHunt.commands.leaderboardheads;
import org.modularsoft.PlayerHeadHunt.events.HeadFindEvent;
import org.modularsoft.PlayerHeadHunt.events.HeadHatOnHead;
import org.modularsoft.PlayerHeadHunt.events.HeadHunterOnJoin;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class PlayerHeadHuntMain extends JavaPlugin {
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

        HeadChatController headChatController = new HeadChatController(this);
        HeadWorldController headWorldController = new HeadWorldController(this);
        HeadHatController headHatController = new HeadHatController(this);
        HeadScoreboardController headScoreboardController = new HeadScoreboardController(this);

        // Connect to the database
        establishConnection();

        // Do an initial calculation of the number of heads. This can be
        // manually recalculated with the relevant command.
        headWorldController.countHeadsInRegion();

        // Plugin Event Register
        PluginManager pluginmanager = getServer().getPluginManager();
        pluginmanager.registerEvents(new HeadFindEvent(this, headWorldController, headChatController, headHatController, headScoreboardController), this);
        pluginmanager.registerEvents(new HeadHunterOnJoin(this, headChatController, headScoreboardController), this);
        pluginmanager.registerEvents(new HeadHatOnHead(), this);

        // Command Registry
        Objects.requireNonNull(getCommand("heads")).setExecutor(new heads(this, headChatController));
        Objects.requireNonNull(getCommand("clearheads")).setExecutor(new clearheads(this, headChatController, headHatController, headScoreboardController));
        Objects.requireNonNull(getCommand("countheads")).setExecutor(new countheads(this, headWorldController, headScoreboardController));
        Objects.requireNonNull(getCommand("leaderboardheads")).setExecutor(new leaderboardheads(this, headChatController));

        // Plugin Load Message
        console.sendMessage(ChatColor.GREEN + getDescription().getName() + " is now enabled.");
        console.sendMessage(ChatColor.GREEN + "Running Version: " + getDescription().getVersion());
        console.sendMessage(ChatColor.GREEN + "GitHub Repository: https://github.com/ModularSoftAU/PlayerHeadHunt");
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
