package org.modularsoft.PlayerHeadHunt.commands;

import org.modularsoft.PlayerHeadHunt.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class clearheads implements CommandExecutor {
    private final PlayerHeadHuntMain plugin;
    private final HeadChatController headChatController;
    private final HeadHatController headHatController;
    private final HeadScoreboardController scoreboardController;
    private final HeadQuery headQuery;

    public clearheads(PlayerHeadHuntMain plugin,
                      HeadChatController headChatController,
                      HeadHatController headHatController,
                      HeadScoreboardController scoreboardController,
                      HeadQuery headQuery) {
        this.plugin = plugin;
        this.headChatController = headChatController;
        this.headHatController = headHatController;
        this.scoreboardController = scoreboardController;
        this.headQuery = headQuery;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.config().getLangNotAPlayer());
            return true;
        }

        if (!sender.hasPermission("playerheadhunt.clearhead") || !sender.isOp()) {
            sender.sendMessage(plugin.config().getLangInsufficientPermissions());
            return true;
        }

        if (!headQuery.clearHeads(player)) // Use the HeadQuery instance to call clearHeads
            return true;

        headChatController.playerClearedTheirHeadsResponse(player);
        headHatController.clearHelmet(player);
        scoreboardController.reloadScoreboard(player, headQuery.foundHeadsCount(player)); // Use headQuery for foundHeadsCount
        return true;
    }
}