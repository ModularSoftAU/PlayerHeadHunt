package org.modularsoft.PlayerHeadHunt.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.modularsoft.PlayerHeadHunt.HeadChatController;
import org.modularsoft.PlayerHeadHunt.HeadQuery;
import org.modularsoft.PlayerHeadHunt.PlayerHeadHuntMain;

import java.util.List;

public class leaderboard implements CommandExecutor {
    private final PlayerHeadHuntMain plugin;
    private final HeadChatController headChatController;

    public leaderboard(PlayerHeadHuntMain plugin, HeadChatController headChatController) {
        this.plugin = plugin;
        this.headChatController = headChatController;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.config().getLangNotAPlayer());
            return true;
        }

        List<HeadQuery.HeadHunter> bestHunters = HeadQuery.getBestHunters(plugin, player, 5);
        headChatController.showLeaderBoardResponse(player, bestHunters);
        return true;
    }
}
