package org.modularsoft.PlayerHeadHunt.commands;

import org.bukkit.Bukkit;
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
    private final HeadQuery headQuery;

    public leaderboard(PlayerHeadHuntMain plugin, HeadChatController headChatController, HeadQuery headQuery) {
        this.plugin = plugin;
        this.headChatController = headChatController;
        this.headQuery = headQuery;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (sender instanceof Player player) {
            // Handle the command for players
            headQuery.getBestHunters(5).thenAccept(bestHunters -> {
                // Must run sync to safely interact with Bukkit API
                Bukkit.getScheduler().runTask(plugin, () -> {
                    headChatController.showLeaderBoardResponse(player, bestHunters);
                });
            });
        } else {
            // Handle the command for the console
            headQuery.getBestHunters(5).thenAccept(bestHunters -> {
                plugin.getServer().getConsoleSender().sendMessage("=== Top 5 Head Hunters ===");
                if (bestHunters.isEmpty()) {
                    plugin.getServer().getConsoleSender().sendMessage("No hunters found.");
                } else {
                    for (int i = 0; i < bestHunters.size(); i++) {
                        HeadQuery.HeadHunter hunter = bestHunters.get(i);
                        plugin.getServer().getConsoleSender().sendMessage(
                                (i + 1) + ". " + hunter.name() + " - " + hunter.headsCollected() + " heads");
                    }
                }
            });
        }
        return true;
    }
}