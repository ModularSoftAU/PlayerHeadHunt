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
    private final HeadQuery headQuery;

    public countheads(PlayerHeadHuntMain plugin,
                      HeadWorldController headWorldController,
                      HeadScoreboardController headScoreboardController,
                      HeadQuery headQuery) {
        this.plugin = plugin;
        this.headWorldController = headWorldController;
        this.headScoreboardController = headScoreboardController;
        this.headQuery = headQuery;
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

        // Count heads in the region
        headWorldController.countHeadsInRegion();

        // Update HEAD.HEADTOTAL in the configuration
        int totalHeads = plugin.config().getTotalHeads();
        plugin.getConfig().set("HEAD.HEADTOTAL", totalHeads);
        plugin.saveConfig(); // Save the updated configuration to disk

        // Notify the sender
        BlockVector3 upper = plugin.config().getUpperRegion();
        BlockVector3 lower = plugin.config().getLowerRegion();
        sender.sendMessage("There are " + totalHeads + " total heads in " + lower + ", " + upper + ".");

        // Update the scoreboard for all online players
        for (Player otherPlayer : plugin.getServer().getOnlinePlayers()) {
            int headsFound = headQuery.foundHeadsCount(otherPlayer);
            headScoreboardController.reloadScoreboard(otherPlayer, headsFound);
        }
        return true;
    }
}
