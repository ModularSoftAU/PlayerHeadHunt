package org.modularsoft.PlayerHeadHunt.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.modularsoft.PlayerHeadHunt.*;

public class debugheadhunt implements CommandExecutor {
    private final PlayerHeadHuntMain plugin;
    private final HeadChatController headChatController;
    private final HeadHatController headHatController;
    private final HeadScoreboardController scoreboardController;
    private final HeadWorldController headWorldController;
    private final HeadQuery headQuery;

    public debugheadhunt(PlayerHeadHuntMain plugin,
                         HeadChatController headChatController,
                         HeadHatController headHatController,
                         HeadScoreboardController scoreboardController,
                         HeadWorldController headWorldController,
                         HeadQuery headQuery) {
        this.plugin = plugin;
        this.headChatController = headChatController;
        this.headHatController = headHatController;
        this.scoreboardController = scoreboardController;
        this.headWorldController = headWorldController;
        this.headQuery = headQuery;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.config().getLangNotAPlayer());
            return true;
        }

        if (!sender.hasPermission("playerheadhunt.debug") || !sender.isOp()) {
            sender.sendMessage(plugin.config().getLangInsufficientPermissions());
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("Usage: /debugheadhunt <clearheads|countheads>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "clearheads" -> {
                if (!headQuery.clearHeads(player)) {
                    sender.sendMessage("No heads to clear.");
                    return true;
                }
                headChatController.playerClearedTheirHeadsResponse(player);
                headHatController.clearHelmet(player);
                scoreboardController.reloadScoreboard(player, headQuery.foundHeadsCount(player));
                sender.sendMessage("Heads cleared successfully.");
            }
            case "countheads" -> {
                headWorldController.countHeadsInRegion();
                sender.sendMessage("Heads counted successfully.");
            }
            default -> sender.sendMessage("Invalid subcommand. Use: clearheads or countheads.");
        }
        return true;
    }
}