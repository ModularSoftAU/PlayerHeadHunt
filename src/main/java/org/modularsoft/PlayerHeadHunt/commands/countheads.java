package org.modularsoft.PlayerHeadHunt.commands;

import org.modularsoft.PlayerHeadHunt.PlayerHeadHuntMain;
import org.modularsoft.PlayerHeadHunt.HeadWorldController;
import org.modularsoft.PlayerHeadHunt.HeadQuery;
import org.modularsoft.PlayerHeadHunt.HeadScoreboardController;
import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class countheads implements CommandExecutor {
    private final PlayerHeadHuntMain plugin;
    private final HeadWorldController headWorldController;
    private final HeadScoreboardController headScoreboardController;

    public countheads(PlayerHeadHuntMain plugin, HeadWorldController headWorldController, HeadScoreboardController headScoreboardController) {
        this.plugin = plugin;
        this.headWorldController = headWorldController;
        this.headScoreboardController = headScoreboardController;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.config().getLangNotAPlayer());
            return true;
        }

        if (!sender.hasPermission("playerheadhunt.clearhead") || !sender.isOp()) {
            sender.sendMessage(plugin.config().getLangInsufficientPermissions());
            return true;
        }

        headWorldController.countHeadsInRegion();
        BlockVector3 upper = plugin.config().getUpperRegion();
        BlockVector3 lower = plugin.config().getLowerRegion();
        sender.sendMessage("There are " + plugin.config().getTotalHeads() +
                " total heads in " + lower + ", " + upper + ".");

        for (Player otherPlayer : plugin.getServer().getOnlinePlayers()) {
            int headsFound = HeadQuery.foundHeadsCount(plugin, otherPlayer);
            headScoreboardController.reloadScoreboard(otherPlayer, headsFound);
        }
        return true;
    }
}
