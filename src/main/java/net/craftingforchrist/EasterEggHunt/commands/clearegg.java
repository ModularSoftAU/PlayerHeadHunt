package net.craftingforchrist.EasterEggHunt.commands;

import net.craftingforchrist.EasterEggHunt.EasterEggHuntMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class clearegg implements CommandExecutor {
    public static EasterEggHuntMain plugin;
    public clearegg(EasterEggHuntMain instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        Player player = (Player) sender;
        String UserUUID = player.getUniqueId().toString();
        String Username = player.getName();

        if (args.equals(0)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.COMMAND.COMMANDINCOMPLETE")));
            return true;
        }

        Player PlayerTarget = Bukkit.getPlayer(args[0]); // Get player by username

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.COMMAND.NOTAPLAYER")));
            return true;
        }

        if (sender.hasPermission("easteregghunt.clearegg") || sender.isOp()) {
            //
            // Database Query
            // Check how many eggs the player has collected.
            //
            try {
                PreparedStatement deletestatement = plugin.getConnection().prepareStatement("DELETE from eastereggs where playerid=(select id from playerdata where uuid=?)");
                deletestatement.setString(1, PlayerTarget.getUniqueId().toString());

                deletestatement.executeUpdate();
                player.sendMessage("All eggs have been cleared from " + Username + ".");
            } catch (SQLException e) {
                e.printStackTrace();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.DATABASE.CONNECTIONERROR")));
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.COMMAND.INSUFFICENTPERMISSIONS")));
        }

        return true;
    }

}
