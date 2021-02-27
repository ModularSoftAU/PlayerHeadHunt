package net.craftingforchrist.EasterEggHunt.commands;

import net.craftingforchrist.EasterEggHunt.EasterEggHuntMain;
import net.craftingforchrist.EasterEggHunt.EggRadius;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class detecteggs implements CommandExecutor {
    public static EasterEggHuntMain plugin;
    public detecteggs(EasterEggHuntMain instance) {
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

        EggRadius.getEggBlocks(sender);

        return true;
    }

}
