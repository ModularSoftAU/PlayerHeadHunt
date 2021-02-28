package net.craftingforchrist.EasterEggHunt;

import org.bukkit.configuration.ConfigurationSection;

public class Variables {
    public static EasterEggHuntMain plugin;
    public Variables(EasterEggHuntMain instance) {
        plugin = instance;
    }

    static ConfigurationSection config = plugin.getConfig();

    // Database
    public static String host = config.getString("database.host");
    public static String port = config.getString("database.port");
    public static String database = config.getString("database.database");
    public static String username = config.getString("database.username");
    public static String password = config.getString("database.password");

    // Sounds

    // Lang
}
