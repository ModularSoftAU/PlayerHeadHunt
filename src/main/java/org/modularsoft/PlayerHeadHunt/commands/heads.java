package org.modularsoft.PlayerHeadHunt.commands;

import org.modularsoft.PlayerHeadHunt.HeadQuery;
import org.modularsoft.PlayerHeadHunt.PlayerHeadHuntMain;
import org.modularsoft.PlayerHeadHunt.HeadChatController;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class heads implements CommandExecutor {
    private final PlayerHeadHuntMain plugin;
    private final HeadChatController headChatController;
    private final HeadQuery headQuery;

    public heads(PlayerHeadHuntMain plugin, HeadChatController headChatController) {
        this.plugin = plugin;
        this.headChatController = headChatController;
        this.headQuery = plugin.getHeadQuery();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (args.length >= 1) {
            // /heads <player> — any sender can look up a specific player
            String targetName = args[0];
            int count = headQuery.foundHeadsCountByName(targetName);
            headChatController.targetPlayerHeadCountResponse(sender, targetName, count);
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.config().getLangNotAPlayer());
            return true;
        }

        headChatController.playersOwnHeadCountResponse(player);
        return true;
    }
}
