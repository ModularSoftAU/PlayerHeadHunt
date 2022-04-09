package com.modularenigma.EasterEggHunt.commands;

import com.modularenigma.EasterEggHunt.EasterEggHuntMain;
import com.modularenigma.EasterEggHunt.EggController;
import com.modularenigma.EasterEggHunt.EggHatController;
import com.modularenigma.EasterEggHunt.EggScoreboardController;
import com.modularenigma.EasterEggHunt.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class cleareggs implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(EasterEggHuntMain.plugin().config().getLangNotAPlayer());
            return true;
        }

        if (!sender.hasPermission("easteregghunt.clearegg") || !sender.isOp()) {
            sender.sendMessage(EasterEggHuntMain.plugin().config().getLangInsufficientPermissions());
            return true;
        }

        EggController.instance().clearEggs(player);
        EggHatController.instance().clearHelmet(player);
        EggScoreboardController.instance().loadSidebarScoreboard(player);
        return true;
    }
}
