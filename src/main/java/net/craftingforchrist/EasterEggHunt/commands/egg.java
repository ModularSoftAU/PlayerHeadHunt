package net.craftingforchrist.EasterEggHunt.commands;

import net.craftingforchrist.EasterEggHunt.EasterEggHuntMain;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class egg implements CommandExecutor {
    public static EasterEggHuntMain plugin;
    public egg(EasterEggHuntMain instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        Player player = (Player) sender;
        String UserUUID = player.getUniqueId().toString();

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.COMMAND.NOTAPLAYER")));
            return true;
        }

        //
        // Database Query
        // Check how many eggs the player has collected.
        //
        try {
            PreparedStatement findstatement = plugin.getConnection().prepareStatement("select count(*) as 'eastereggs' from eastereggs where playerid = (select id from playerdata where uuid=?)");
            findstatement.setString(1, UserUUID);

            ResultSet results = findstatement.executeQuery();
            if (results.next()) {
                int totaleggs = results.getInt("eastereggs");
                String MILESTONEREACHEDMESSAGE = plugin.getConfig().getString("LANG.EGG.EGGCOUNT");

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', MILESTONEREACHEDMESSAGE.replace("%NUMBEROFEGGS%", String.valueOf(totaleggs))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.DATABASE.CONNECTIONERROR")));
        }

        return true;
    }

}
