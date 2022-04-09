package com.modularenigma.EasterEggHunt.events;

import com.modularenigma.EasterEggHunt.EasterEggHuntMain;
import com.modularenigma.EasterEggHunt.EggChatController;
import com.modularenigma.EasterEggHunt.EggQuery;
import com.modularenigma.EasterEggHunt.EggScoreboardController;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EggHunterOnJoin implements Listener {
    private final EasterEggHuntMain plugin;
    private final EggChatController eggChatController;
    private final EggScoreboardController eggScoreboardController;

    public EggHunterOnJoin(EasterEggHuntMain plugin, EggChatController eggChatController,
                           EggScoreboardController eggScoreboardController) {
        this.plugin = plugin;
        this.eggChatController = eggChatController;
        this.eggScoreboardController = eggScoreboardController;
    }

    @EventHandler
    public void onEggHunterJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String username = player.getName();

        // Give the new player a scoreboard
        eggScoreboardController.reloadScoreboard(player, EggQuery.foundEggsCount(plugin, player));

        if (EggQuery.addNewHunter(plugin, player)) {
            // New player joined
            plugin.getServer().getConsoleSender().sendMessage(username + " is a new player, creating a player profile.");
            plugin.getServer().getConsoleSender().sendMessage("Added a new hunter, " + username + ".");
            eggChatController.newPlayerJoinsTheHunt(player);
        }
    }
}
