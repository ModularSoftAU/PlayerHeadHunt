package com.modularenigma.EasterEggHunt.commands;

import com.modularenigma.EasterEggHunt.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class leaderboardeggs implements CommandExecutor {
    private final EasterEggHuntMain plugin;
    private final EggChatController eggChatController;

    public leaderboardeggs(EasterEggHuntMain plugin, EggChatController eggChatController) {
        this.plugin = plugin;
        this.eggChatController = eggChatController;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.config().getLangNotAPlayer());
            return true;
        }

        List<EggQuery.EggHunter> bestHunters = EggQuery.getBestHunters(plugin, player, 5);
        eggChatController.showLeaderBoardResponse(player, bestHunters);
        return true;
    }
}
