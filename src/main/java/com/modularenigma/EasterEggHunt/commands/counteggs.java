package com.modularenigma.EasterEggHunt.commands;

import com.modularenigma.EasterEggHunt.EasterEggHuntMain;
import com.modularenigma.EasterEggHunt.EggWorldController;
import com.modularenigma.EasterEggHunt.EggQuery;
import com.modularenigma.EasterEggHunt.EggScoreboardController;
import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class counteggs implements CommandExecutor {
    private final EasterEggHuntMain plugin;
    private final EggWorldController eggWorldController;
    private final EggScoreboardController eggScoreboardController;

    public counteggs(EasterEggHuntMain plugin, EggWorldController eggWorldController, EggScoreboardController eggScoreboardController) {
        this.plugin = plugin;
        this.eggWorldController = eggWorldController;
        this.eggScoreboardController = eggScoreboardController;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.config().getLangNotAPlayer());
            return true;
        }

        if (!sender.hasPermission("easteregghunt.clearegg") || !sender.isOp()) {
            sender.sendMessage(plugin.config().getLangInsufficientPermissions());
            return true;
        }

        eggWorldController.countEggsInRegion();
        BlockVector3 upper = plugin.config().getUpperRegion();
        BlockVector3 lower = plugin.config().getLowerRegion();
        sender.sendMessage("There are " + plugin.config().getTotalEggs() +
                " total eggs in " + lower + ", " + upper + ".");

        for (Player otherPlayer : plugin.getServer().getOnlinePlayers()) {
            int eggsFound = EggQuery.foundEggsCount(plugin, otherPlayer);
            eggScoreboardController.reloadScoreboard(otherPlayer, eggsFound);
        }
        return true;
    }
}
