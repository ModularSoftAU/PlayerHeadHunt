package org.modularsoft.PlayerHeadHunt.events;

import org.modularsoft.PlayerHeadHunt.PlayerHeadHuntMain;
import org.modularsoft.PlayerHeadHunt.HeadChatController;
import org.modularsoft.PlayerHeadHunt.HeadHatController;
import org.modularsoft.PlayerHeadHunt.HeadQuery;
import org.modularsoft.PlayerHeadHunt.HeadScoreboardController;
import org.modularsoft.PlayerHeadHunt.compass.HeadCompassController;
import org.modularsoft.PlayerHeadHunt.helpers.HeadMileStone;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;

public class HeadHunterOnJoin implements Listener {
    private final PlayerHeadHuntMain plugin;
    private final HeadChatController headChatController;
    private final HeadScoreboardController headScoreboardController;
    private final HeadQuery headQuery;
    private final HeadHatController headHatController;
    private final HeadCompassController compassController;

    public HeadHunterOnJoin(PlayerHeadHuntMain plugin,
                            HeadChatController headChatController,
                            HeadScoreboardController headScoreboardController,
                            HeadQuery headQuery,
                            HeadHatController headHatController,
                            HeadCompassController compassController) {
        this.plugin = plugin;
        this.headChatController = headChatController;
        this.headScoreboardController = headScoreboardController;
        this.headQuery = headQuery;
        this.headHatController = headHatController;
        this.compassController = compassController;
    }

    @EventHandler
    public void onHeadHunterJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String username = player.getName();

        int headCount = headQuery.foundHeadsCount(player);

        // Use the instance of HeadQuery to call the method
        headScoreboardController.reloadScoreboard(player, headCount);

        // Re-apply the highest milestone helmet the player has earned
        if (plugin.config().isMilestoneHatFeatureEnabled()) {
            plugin.config().getHeadMilestones().entrySet().stream()
                .filter(e -> e.getValue().getHelmet() != null && e.getKey() <= headCount)
                .max(Map.Entry.comparingByKey())
                .ifPresent(e -> headHatController.equipHelmet(player, e.getValue().getHelmet()));
        }

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