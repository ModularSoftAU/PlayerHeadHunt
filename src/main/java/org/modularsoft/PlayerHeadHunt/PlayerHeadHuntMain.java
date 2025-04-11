package org.modularsoft.PlayerHeadHunt;

import lombok.Getter;
import org.modularsoft.PlayerHeadHunt.commands.clearheads;
import org.modularsoft.PlayerHeadHunt.commands.heads;
import org.modularsoft.PlayerHeadHunt.commands.countheads;
import org.modularsoft.PlayerHeadHunt.commands.leaderboard;
import org.modularsoft.PlayerHeadHunt.events.HeadFindEvent;
import org.modularsoft.PlayerHeadHunt.events.HeadHatOnHead;
import org.modularsoft.PlayerHeadHunt.events.HeadHunterOnJoin;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.modularsoft.PlayerHeadHunt.helpers.YamlFileManager;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class PlayerHeadHuntMain extends JavaPlugin {
    private PluginConfig config;
    private ConsoleCommandSender console;

    public PluginConfig config() {
        return config;
    }

    @Getter
    private HeadQuery headQuery;

    @Override
    public void onEnable() {
        // Generate configuration file
        saveDefaultConfig();
        config = new PluginConfig(this);
        console = getServer().getConsoleSender();

        YamlFileManager yamlFileManager = new YamlFileManager(new File(getDataFolder(), "player-data.yml"));
        headQuery = new HeadQuery(yamlFileManager);

        HeadChatController headChatController = new HeadChatController(this, headQuery);
        HeadWorldController headWorldController = new HeadWorldController(this);
        HeadHatController headHatController = new HeadHatController(this);
        HeadScoreboardController headScoreboardController = new HeadScoreboardController(this);

        // Do an initial calculation of the number of heads. This can be
        // manually recalculated with the relevant command.
        headWorldController.countHeadsInRegion();

        // Plugin Event Register
        PluginManager pluginmanager = getServer().getPluginManager();
        pluginmanager.registerEvents(new HeadFindEvent(this, headWorldController, headChatController, headHatController, headScoreboardController, headQuery), this);
        pluginmanager.registerEvents(new HeadHunterOnJoin(this, headChatController, headScoreboardController, headQuery), this);
        pluginmanager.registerEvents(new HeadHatOnHead(), this);

        // Command Registry
        Objects.requireNonNull(getCommand("heads")).setExecutor(new heads(this, headChatController));
        Objects.requireNonNull(getCommand("clearheads")).setExecutor(new clearheads(this, headChatController, headHatController, headScoreboardController, headQuery));
        Objects.requireNonNull(getCommand("countheads")).setExecutor(new countheads(this, headWorldController, headScoreboardController, headQuery));
        Objects.requireNonNull(getCommand("heads")).setExecutor(new heads(this, headChatController));
        Objects.requireNonNull(getCommand("leaderboard")).setExecutor(new leaderboard(this, headChatController, headQuery)); // Register leaderboard command

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
}
