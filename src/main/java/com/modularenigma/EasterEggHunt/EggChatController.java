package com.modularenigma.EasterEggHunt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class EggChatController {
    private final EasterEggHuntMain plugin;

    public EggChatController(EasterEggHuntMain plugin) {
        this.plugin = plugin;
    }

    public void eggAlreadyFoundResponse(Player player) {
        player.playSound(player.getLocation(), plugin.config().getEggAlreadyFoundSound(), 1, 1); // Play sound for an Easter Egg that is already found.
        player.sendMessage(plugin.config().getLangEggAlreadyFound());
    }

    public void eggFoundResponse(Player player, int eggCount) {
        String message = plugin.config().getLangEggFound()
                .replace("%FOUNDEGGS%", eggCount + "")
                .replace("%NUMBEROFEGGS%", "" + plugin.config().getTotalEggs());

        // Play sound for an Easter Egg that is found.
        player.playSound(player.getLocation(), plugin.config().getEggFoundSound(), 1, 1);
        player.sendMessage(message);
    }

    public void eggMilestoneReachedEvent(Player player, boolean isMajorSound, int eggs) {
        if (!plugin.config().isMilestoneMessageFeatureEnabled())
            return;

        Sound majorSound = plugin.config().getMajorCollectionSound();
        Sound minorSound = plugin.config().getMinorCollectionSound();

        if (isMajorSound)
            player.playSound(player.getLocation(), majorSound, 1, 1);
        else
            player.playSound(player.getLocation(), minorSound, 1, 1);

        String broadcastMessage = plugin.config().getLangEggCollectionMilestoneReached()
                .replace("%PLAYER%", player.getName())
                .replace("%NUMBEROFEGGS%", String.valueOf(eggs));

        // Tell other players about the milestone
        World world = player.getWorld();

        for (Player otherPlayers : Bukkit.getOnlinePlayers()) {
            otherPlayers.sendMessage(broadcastMessage);
            world.playSound(player.getLocation(), minorSound, 1, 1);
        }
    }

    public void newPlayerJoinsTheHunt(Player player) {
        player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Welcome Egg Hunter.");
        player.sendMessage("Welcome Egg Hunter to the Easter Egg Hunt. Explore our Hub and the fields outside and collect as many eggs as you can.");
        player.sendMessage("Right Click to collect an Easter Egg and you will hear a ding when it is collected.");
        player.sendMessage(" ");
        player.sendMessage(ChatColor.YELLOW + "Happy Easter and happy hunting.\nFrom Crafting For Christ");
    }

    public void playersOwnEggCountResponse(Player player) {
        player.sendMessage(plugin.config().getLangEggCount()
                .replace("%FOUNDEGGS%", "" + EggQuery.foundEggsCount(plugin, player))
                .replace("%NUMBEROFEGGS%", "" + plugin.config().getTotalEggs()));
    }

    public void playerClearedTheirEggsResponse(Player player) {
        player.sendMessage("All eggs have been cleared.");
    }

    public void showHunterStatsResponse(Player player, EggQuery.EggHunter hunter, int rank) {
        player.sendMessage(rank + ". " + hunter.name() + " with " + hunter.eggsCollected() + " eggs.");
    }
}
