package net.craftingforchrist.EasterEggHunt;

import net.craftingforchrist.EasterEggHunt.commands.clearegg;
import net.craftingforchrist.EasterEggHunt.commands.egg;
import net.craftingforchrist.EasterEggHunt.events.EggFindEvent;
import net.craftingforchrist.EasterEggHunt.events.EggHunterOnJoin;
import net.craftingforchrist.EasterEggHunt.events.EggMilestoneReachedEvent;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class EasterEggHuntMain extends JavaPlugin {
    public static EasterEggHuntMain plugin;
    private Connection connection;

    public Variables Variables;
    public EggController EggController;

    @Override
    public void onEnable() {
        plugin = this;

//        Variables Variables = new Variables(this);
        EggController EggController = new EggController(this);

        establishConnection(); // Connect to the database
        EggController.setTotalEggBlocks();

        // Plugin Load Message
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "\n\n" + plugin.getDescription().getName() + " is now enabled.\nRunning Version: " + plugin.getDescription().getVersion() + "\nGitHub Repository: https://github.com/craftingforchrist/EasterEggHunt\nCreated By: " + plugin.getDescription().getAuthors() + "\n\n");

        // Plugin Event Register
        PluginManager pluginmanager = plugin.getServer().getPluginManager();
        pluginmanager.registerEvents(new EggFindEvent(this), this);
        pluginmanager.registerEvents(new EggHunterOnJoin(this), this);
        pluginmanager.registerEvents(new EggMilestoneReachedEvent(this), this);

        // Command Registry
        this.getCommand("egg").setExecutor(new egg(this));
        this.getCommand("clearegg").setExecutor(new clearegg(this));

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
