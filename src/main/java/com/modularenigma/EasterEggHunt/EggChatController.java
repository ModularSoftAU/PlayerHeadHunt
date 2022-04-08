package com.modularenigma.EasterEggHunt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class EggChatController {
    private static EasterEggHuntMain plugin;
    private static EggChatController instance;
    private final Sound foundEggSound;
    private final Sound alreadyFoundEggSound;
    private final String foundEgg;
    private final String alreadyFoundEgg;
    private final String totalEggs;

    public static void onEnable(EasterEggHuntMain plugin) {
        EggChatController.plugin = plugin;
    }

    public static EggChatController instance() {
        assert plugin != null;
        if (instance == null)
            instance = new EggChatController();
        return instance;
    }

    private EggChatController() {
        foundEggSound = Sound.valueOf(plugin.getConfig().getString("SOUND.EGGFOUND"));
        alreadyFoundEggSound = Sound.valueOf(plugin.getConfig().getString("SOUND.EGGALREADYFOUND"));
        totalEggs = plugin.getConfig().getString("EGG.EGGTOTAL");
        alreadyFoundEgg = plugin.getConfig().getString("LANG.EGG.EGGALREADYFOUND");
        foundEgg = plugin.getConfig().getString("LANG.EGG.EGGFOUND");
    }

    public void eggAlreadyFoundResponse(Player player) {
        player.playSound(player.getLocation(), alreadyFoundEggSound, 1, 1); // Play sound for an Easter Egg that is already found.
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', alreadyFoundEgg));
    }

    public void eggFoundResponse(Player player) {
        int playerHasFound = EggController.instance().getEggs(player);

        String message = ChatColor.translateAlternateColorCodes('&', foundEgg)
                .replace("%FOUNDEGGS%", playerHasFound + "")
                .replace("%NUMBEROFEGGS%", totalEggs);
        player.playSound(player.getLocation(), foundEggSound, 1, 1); // Play sound for an Easter Egg that is found.
        player.sendMessage(message);
    }

    public void eggMilestoneReachedEvent(Player player, Sound eggSound, int eggs) {
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
