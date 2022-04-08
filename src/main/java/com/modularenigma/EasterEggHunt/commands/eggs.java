package com.modularenigma.EasterEggHunt.commands;

import com.modularenigma.EasterEggHunt.EasterEggHuntMain;
import com.modularenigma.EasterEggHunt.EggController;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class eggs implements CommandExecutor {
    public final EasterEggHuntMain plugin;
    private final String notPlayerMessage;

    public eggs(EasterEggHuntMain plugin) {
        this.plugin = plugin;

        String blankNotPlayer = plugin.getConfig().getString("LANG.COMMAND.NOTAPLAYER");
        assert blankNotPlayer != null;
        notPlayerMessage = ChatColor.translateAlternateColorCodes('&', blankNotPlayer);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(notPlayerMessage);
            return true;
        }

        String numberOfEggs = String.valueOf(plugin.getConfig().getInt("EGG.EGGTOTAL"));
        String blankMilestoneReachedMessage = plugin.getConfig().getString("LANG.EGG.EGGCOUNT");
        assert blankMilestoneReachedMessage != null;
        String milestoneReachedMessage = blankMilestoneReachedMessage
                .replace("%FOUNDEGGS%", "" + EggController.instance().getEggs(player))
                .replace("%NUMBEROFEGGS%", numberOfEggs);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', milestoneReachedMessage));
        return true;
    }

}
