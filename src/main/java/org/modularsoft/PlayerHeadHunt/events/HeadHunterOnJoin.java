package org.modularsoft.PlayerHeadHunt.events;

import org.modularsoft.PlayerHeadHunt.PlayerHeadHuntMain;
import org.modularsoft.PlayerHeadHunt.HeadChatController;
import org.modularsoft.PlayerHeadHunt.HeadQuery;
import org.modularsoft.PlayerHeadHunt.HeadScoreboardController;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class HeadHunterOnJoin implements Listener {
    private final PlayerHeadHuntMain plugin;
    private final HeadChatController headChatController;
    private final HeadScoreboardController headScoreboardController;

    public HeadHunterOnJoin(PlayerHeadHuntMain plugin, HeadChatController headChatController,
                            HeadScoreboardController headScoreboardController) {
        this.plugin = plugin;
        this.headChatController = headChatController;
        this.headScoreboardController = headScoreboardController;
    }

    @EventHandler
    public void onHeadHunterJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String username = player.getName();

        // Give the new player a scoreboard
        headScoreboardController.reloadScoreboard(player, HeadQuery.foundHeadsCount(plugin, player));

        if (HeadQuery.addNewHunter(plugin, player)) {
            // New player joined
            plugin.getServer().getConsoleSender().sendMessage(username + " is a new player, creating a player profile.");
            plugin.getServer().getConsoleSender().sendMessage("Added a new hunter, " + username + ".");
            headChatController.newPlayerJoinsTheHunt(player);
        }
    }
}
