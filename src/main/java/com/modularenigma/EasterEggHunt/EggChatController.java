package com.modularenigma.EasterEggHunt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class EggChatController {
    private static EggChatController instance;

    public static EggChatController instance() {
        if (instance == null)
            instance = new EggChatController();
        return instance;
    }

    private EggChatController() {}

    public void eggAlreadyFoundResponse(Player player) {
        player.playSound(player.getLocation(), EasterEggHuntMain.plugin().config().getEggAlreadyFoundSound(), 1, 1); // Play sound for an Easter Egg that is already found.
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', EasterEggHuntMain.plugin().config().getLangEggAlreadyFound()));
    }

    public void eggFoundResponse(Player player) {
        int playerHasFound = EggController.instance().getEggs(player);

        String message = ChatColor.translateAlternateColorCodes('&', EasterEggHuntMain.plugin().config().getLangEggFound())
                .replace("%FOUNDEGGS%", playerHasFound + "")
                .replace("%NUMBEROFEGGS%", "" + EasterEggHuntMain.plugin().config().getTotalEggs());

        // Play sound for an Easter Egg that is found.
        player.playSound(player.getLocation(), EasterEggHuntMain.plugin().config().getEggFoundSound(), 1, 1);
        player.sendMessage(message);
    }

    public void eggMilestoneReachedEvent(Player player, Sound eggSound, int eggs) {
        if (EasterEggHuntMain.plugin().config().isMilestoneHatFeatureEnabled()) {
            player.playSound(player.getLocation(), eggSound, 1, 1);

            String broadcastMessage = ChatColor.translateAlternateColorCodes('&', EasterEggHuntMain.plugin().config().getLangEggCollectionMilestoneReached())
                    .replace("%PLAYER%", player.getName())
                    .replace("%NUMBEROFEGGS%", String.valueOf(eggs));
            for (Player otherPlayers : Bukkit.getOnlinePlayers()) {
                otherPlayers.sendMessage(broadcastMessage);
            }
        }
    }
}
