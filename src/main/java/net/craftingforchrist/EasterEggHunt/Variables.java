package net.craftingforchrist.EasterEggHunt;

import org.bukkit.configuration.file.FileConfiguration;

public class Variables {
    private static EasterEggHuntMain plugin;
    public Variables(EasterEggHuntMain plugin) {
        this.plugin = plugin;
    }

    private FileConfiguration config;

    public Variables(FileConfiguration config) { this.config = config; }

//    FileConfiguration config = plugin.getConfig();

    // Database
    static String host = plugin.getConfig().getString("database.host");
    String port = config.getString("database.port");
    String database = config.getString("database.database");
    String username = config.getString("database.username");
    String password = config.getString("database.password");

    // Egg
    String EGGTOTAL = String.valueOf(config.get("EGG.EGGTOTAL"));

    // Sounds

    // Lang
}
