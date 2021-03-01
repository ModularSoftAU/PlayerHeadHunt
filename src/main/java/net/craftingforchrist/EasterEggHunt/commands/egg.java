package net.craftingforchrist.EasterEggHunt.commands;

import net.craftingforchrist.EasterEggHunt.EasterEggHuntMain;
import net.craftingforchrist.EasterEggHunt.EggController;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class egg implements CommandExecutor {
    public static EasterEggHuntMain plugin;
    public egg(EasterEggHuntMain instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        Player player = (Player) sender;

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.COMMAND.NOTAPLAYER")));
            return true;
        }

        String NUMBEROFEGGS = String.valueOf(plugin.getConfig().getInt("EGG.EGGTOTAL"));
        String MILESTONEREACHEDMESSAGE = plugin.getConfig().getString("LANG.EGG.EGGCOUNT");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', MILESTONEREACHEDMESSAGE.replace("%FOUNDEGGS%", String.valueOf(EggController.getEggs(player))).replace("%NUMBEROFEGGS%", NUMBEROFEGGS)));

        return true;
    }

}
