package org.modularsoft.PlayerHeadHunt.commands;

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

    public heads(PlayerHeadHuntMain plugin, HeadChatController headChatController) {
        this.plugin = plugin;
        this.headChatController = headChatController;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.config().getLangNotAPlayer());
            return true;
        }

        headChatController.playersOwnHeadCountResponse(player);
        return true;
    }

}
