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

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(EasterEggHuntMain.plugin().config().getLangNotAPlayer());
            return true;
        }

        player.sendMessage(EasterEggHuntMain.plugin().config().getLangEggCollectionMilestoneReached()
                .replace("%FOUNDEGGS%", "" + EggController.instance().getEggs(player))
                .replace("%NUMBEROFEGGS%", "" + EasterEggHuntMain.plugin().config().getTotalEggs()));
        return true;
    }

}
