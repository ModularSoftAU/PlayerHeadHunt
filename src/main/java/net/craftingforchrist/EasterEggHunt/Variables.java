package net.craftingforchrist.EasterEggHunt;

public class Variables {
    public static EasterEggHuntMain plugin;
    public Variables(EasterEggHuntMain instance) {
        plugin = instance;
    }

    // Database
    public static String host = plugin.getConfig().getString("database.host");
    public static String port = plugin.getConfig().getString("database.port");
    public static String database = plugin.getConfig().getString("database.database");
    public static String username = plugin.getConfig().getString("database.username");
    public static String password = plugin.getConfig().getString("database.password");
}
