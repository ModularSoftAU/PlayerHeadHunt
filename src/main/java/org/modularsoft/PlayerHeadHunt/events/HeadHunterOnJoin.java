package org.modularsoft.PlayerHeadHunt.events;

import org.modularsoft.PlayerHeadHunt.PlayerHeadHuntMain;
import org.modularsoft.PlayerHeadHunt.HeadChatController;
import org.modularsoft.PlayerHeadHunt.HeadQuery;
import org.modularsoft.PlayerHeadHunt.HeadScoreboardController;
import org.modularsoft.PlayerHeadHunt.compass.HeadCompassController;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class HeadHunterOnJoin implements Listener {
    private final PlayerHeadHuntMain plugin;
    private final HeadChatController headChatController;
    private final HeadScoreboardController headScoreboardController;
    private final HeadQuery headQuery;
    private final HeadCompassController compassController;

    public HeadHunterOnJoin(PlayerHeadHuntMain plugin,
                            HeadChatController headChatController,
                            HeadScoreboardController headScoreboardController,
                            HeadQuery headQuery,
                            HeadCompassController compassController) {
        this.plugin = plugin;
        this.headChatController = headChatController;
        this.headScoreboardController = headScoreboardController;
        this.headQuery = headQuery;
        this.compassController = compassController;
    }

    @EventHandler
    public void onHeadHunterJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String username = player.getName();

        // Use the instance of HeadQuery to call the method
        headScoreboardController.reloadScoreboard(player, headQuery.foundHeadsCount(player));

        if (headQuery.addNewHunter(player)) {
            // New player joined
            plugin.getServer().getConsoleSender().sendMessage(username + " is a new player, creating a player profile.");
            plugin.getServer().getConsoleSender().sendMessage("Added a new hunter, " + username + ".");
            headChatController.newPlayerJoinsTheHunt(player);
        }

        if (plugin.config().isCompassEnabled()) {
            compassController.onPlayerJoin(player);
        }
    }
}