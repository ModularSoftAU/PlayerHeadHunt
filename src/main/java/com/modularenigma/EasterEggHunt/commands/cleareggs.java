package com.modularenigma.EasterEggHunt.commands;

import com.modularenigma.EasterEggHunt.EasterEggHuntMain;
import com.modularenigma.EasterEggHunt.EggController;
import com.modularenigma.EasterEggHunt.EggHatController;
import com.modularenigma.EasterEggHunt.EggScoreboardController;
import com.modularenigma.EasterEggHunt.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class cleareggs implements CommandExecutor {
    public final EasterEggHuntMain plugin;
    private final String notPlayerMessage;
    private final String insufficientPermissions;

    public cleareggs(EasterEggHuntMain plugin) {
        this.plugin = plugin;

        String blankNotPlayer = plugin.getConfig().getString("LANG.COMMAND.NOTAPLAYER");
        assert blankNotPlayer != null;
        notPlayerMessage = ChatColor.translateAlternateColorCodes('&', blankNotPlayer);

        String blankInsufficientPermissions = plugin.getConfig().getString("LANG.COMMAND.INSUFFICENTPERMISSIONS");
        assert blankInsufficientPermissions != null;
        insufficientPermissions = ChatColor.translateAlternateColorCodes('&', blankInsufficientPermissions);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(notPlayerMessage);
            return true;
        }

        if (!sender.hasPermission("easteregghunt.clearegg") || !sender.isOp()) {
            sender.sendMessage(insufficientPermissions);
            return true;
        }

        EggController.clearEggs(player);
        EggHatController.clearHelmet(player);
        EggScoreboardController.loadSidebarScoreboard(player);
        return true;
    }
}
