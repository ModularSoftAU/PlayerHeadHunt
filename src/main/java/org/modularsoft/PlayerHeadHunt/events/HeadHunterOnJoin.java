package org.modularsoft.PlayerHeadHunt.events;

import org.modularsoft.PlayerHeadHunt.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class HeadHunterOnJoin implements Listener {
    private final PlayerHeadHuntMain plugin;
    private final HeadChatController headChatController;
    private final HeadScoreboardController headScoreboardController;
    private final HeadQuery headQuery;
    private final HeadWorldController headWorldController; // Add this field

    public HeadHunterOnJoin(PlayerHeadHuntMain plugin,
                            HeadChatController headChatController,
                            HeadScoreboardController headScoreboardController,
                            HeadQuery headQuery,
                            HeadWorldController headWorldController) {
        this.plugin = plugin;
        this.headChatController = headChatController;
        this.headScoreboardController = headScoreboardController;
        this.headQuery = headQuery;
        this.headWorldController = headWorldController; // Initialize it here
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

        // Remove all existing Head Locator items from the player's inventory
        player.getInventory().forEach(item -> {
            if (item != null &&
                    item.hasItemMeta() &&
                    item.getItemMeta().hasDisplayName() &&
                    item.getItemMeta().getDisplayName().equals("§6Head Locator")) {
                player.getInventory().remove(item);
            }
        });

        // Add a new Head Locator compass
        player.getInventory().addItem(org.modularsoft.PlayerHeadHunt.items.HeadLocator.createHeadLocator());

        // Start the HeadLocatorTask for the player
        new org.modularsoft.PlayerHeadHunt.helpers.HeadLocatorTask(plugin, new org.modularsoft.PlayerHeadHunt.items.HeadLocator(), headWorldController).startTask(player);
    }
}