package org.modularsoft.PlayerHeadHunt.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.modularsoft.PlayerHeadHunt.*;
import org.modularsoft.PlayerHeadHunt.helpers.WebhookUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class debugheadhunt implements CommandExecutor, TabCompleter {
    private final PlayerHeadHuntMain plugin;
    private final HeadChatController headChatController;
    private final HeadHatController headHatController;
    private final HeadScoreboardController scoreboardController;
    private final HeadWorldController headWorldController;
    private final HeadQuery headQuery;
    private final WebhookUtil webhookUtil;

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
        this.webhookUtil = new WebhookUtil(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!sender.hasPermission("playerheadhunt.debug") || !sender.isOp()) {
            sender.sendMessage(plugin.config().getLangInsufficientPermissions());
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("Usage: /debugheadhunt <clearheads|countheads|firewebhook>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "clearheads" -> {
                if (sender instanceof Player player) {
                    if (!headQuery.clearHeads(player)) {
                        sender.sendMessage("No heads to clear.");
                        return true;
                    }
                    headChatController.playerClearedTheirHeadsResponse(player);
                    headHatController.clearHelmet(player);
                    scoreboardController.reloadScoreboard(player, headQuery.foundHeadsCount(player));
                    sender.sendMessage("Heads cleared successfully.");
                } else {
                    sender.sendMessage("The 'clearheads' command can only be executed by a player.");
                }
            }
            case "countheads" -> {
                headWorldController.countHeadsInRegion();
                sender.sendMessage("Heads counted successfully.");
            }
            case "firewebhook" -> {
                try {
                    String webhookUrl = plugin.getConfig().getString("DISCORD.WEBHOOKURL");
                    webhookUtil.sendLeaderboardWebhook(webhookUrl, headQuery);
                    sender.sendMessage("Webhook fired successfully.");
                } catch (Exception e) {
                    sender.sendMessage("Failed to fire webhook: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            default -> sender.sendMessage("Invalid subcommand. Use: clearheads, countheads, or firewebhook.");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("clearheads", "countheads", "firewebhook");
        }
        return Collections.emptyList();
    }
}