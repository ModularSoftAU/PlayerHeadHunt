package com.modularenigma.EasterEggHunt.commands;

import com.modularenigma.EasterEggHunt.EasterEggHuntMain;
import com.modularenigma.EasterEggHunt.EggHatController;
import com.modularenigma.EasterEggHunt.EggScoreboardController;
import com.modularenigma.EasterEggHunt.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class cleareggs implements CommandExecutor {
    private final EasterEggHuntMain plugin;
    private final EggChatController eggChatController;
    private final EggHatController eggHatController;
    private final EggScoreboardController scoreboardController;

    public cleareggs(EasterEggHuntMain plugin,  EggChatController eggChatController,
                     EggHatController eggHatController, EggScoreboardController scoreboardController) {
        this.plugin = plugin;
        this.eggChatController = eggChatController;
        this.eggHatController = eggHatController;
        this.scoreboardController = scoreboardController;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.config().getLangNotAPlayer());
            return true;
        }

        if (!sender.hasPermission("easteregghunt.clearegg") || !sender.isOp()) {
            sender.sendMessage(plugin.config().getLangInsufficientPermissions());
            return true;
        }

        if (!EggQuery.clearEggs(plugin, player))
            return true;

        eggChatController.playerClearedTheirEggsResponse(player);
        eggHatController.clearHelmet(player);
        scoreboardController.reloadScoreboard(player, EggQuery.foundEggsCount(plugin, player));
        return true;
    }
}
