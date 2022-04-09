package com.modularenigma.EasterEggHunt.commands;

import com.modularenigma.EasterEggHunt.EasterEggHuntMain;
import com.modularenigma.EasterEggHunt.EggChatController;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class eggs implements CommandExecutor {
    private final EasterEggHuntMain plugin;
    private final EggChatController eggChatController;

    public eggs(EasterEggHuntMain plugin, EggChatController eggChatController) {
        this.plugin = plugin;
        this.eggChatController = eggChatController;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.config().getLangNotAPlayer());
            return true;
        }

        eggChatController.playersOwnEggCountResponse(player);
        return true;
    }

}
