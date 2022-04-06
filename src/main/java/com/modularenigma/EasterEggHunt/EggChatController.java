package com.modularenigma.EasterEggHunt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class EggChatController {
    private static EasterEggHuntMain plugin;

    public static void onEnable(EasterEggHuntMain plugin) {
        EasterEggHuntMain.plugin = plugin;
    }

    public static void eggAlreadyFoundResponse(Player player) {
        Sound eggAlreadyFoundSound = Sound.valueOf(plugin.getConfig().getString("SOUND.EGGALREADYFOUND"));
        String eggAlreadyFound = plugin.getConfig().getString("LANG.EGG.EGGALREADYFOUND");

        assert eggAlreadyFound != null;

        player.playSound(player.getLocation(), eggAlreadyFoundSound, 1, 1); // Play sound for an Easter Egg that is already found.
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', eggAlreadyFound));
    }

    public static void eggFoundResponse(Player player) {
        Sound foundEggSound = Sound.valueOf(plugin.getConfig().getString("SOUND.EGGFOUND"));
        String totalEggs = plugin.getConfig().getString("EGG.EGGTOTAL");
        String foundEgg = plugin.getConfig().getString("LANG.EGG.EGGFOUND");
        int playerHasFound = EggController.getEggs(player);

        assert foundEgg != null;
        assert totalEggs != null;

        String message = ChatColor.translateAlternateColorCodes('&', foundEgg)
                .replace("%FOUNDEGGS%", playerHasFound + "")
                .replace("%NUMBEROFEGGS%", totalEggs);
        player.playSound(player.getLocation(), foundEggSound, 1, 1); // Play sound for an Easter Egg that is found.
        player.sendMessage(message);
    }

    public static void eggMilestoneReachedEvent(Player player, Sound eggSound, int eggs) {
        boolean showMilestoneMessage = plugin.getConfig().getBoolean("FEATURE.MILESTONEMESSAGE");
        String milestoneReached = plugin.getConfig().getString("LANG.EGG.EGGCOLLECTIONMILESTONEREACHED");

        assert milestoneReached != null;

        if (showMilestoneMessage) {
            player.playSound(player.getLocation(), eggSound, 1, 1);

            String broadcastMessage = ChatColor.translateAlternateColorCodes('&', milestoneReached)
                    .replace("%PLAYER%", player.getName())
                    .replace("%NUMBEROFEGGS%", String.valueOf(eggs));
            for (Player otherPlayers : Bukkit.getOnlinePlayers()) {
                otherPlayers.sendMessage(broadcastMessage);
            }
        }
    }
}
