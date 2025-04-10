package org.modularsoft.PlayerHeadHunt.events;

import org.bukkit.Material;
import org.modularsoft.PlayerHeadHunt.*;
import org.modularsoft.PlayerHeadHunt.helpers.HeadMileStone;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;

public class HeadFindEvent implements Listener {
    private final PlayerHeadHuntMain plugin;
    private final HeadWorldController headWorldController;
    private final HeadChatController headChatController;
    private final HeadHatController headHatController;
    private final HeadScoreboardController headScoreboardController;
    private final Map<Integer, HeadMileStone> milestones;

    private final HeadQuery headQuery;

    public HeadFindEvent(PlayerHeadHuntMain plugin, HeadWorldController headWorldController,
                         HeadChatController headChatController, HeadHatController headHatController,
                         HeadScoreboardController headScoreboardController, HeadQuery headQuery) {
        this.plugin = plugin;
        this.headWorldController = headWorldController;
        this.headChatController = headChatController;
        this.headHatController = headHatController;
        this.headScoreboardController = headScoreboardController;
        this.headQuery = headQuery;
        this.milestones = plugin.config().getHeadMilestones();
    }

    @EventHandler
    public void onHeadFind(PlayerInteractEvent event) {
        if (!isFindHeadEvent(event))
            return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        if (headQuery.hasAlreadyCollectedHead(player, x, y, z)) {
            headChatController.headFoundResponse(player, true, 0, x, y, z);
            return;
        }

        headWorldController.playerCollectedHead(player, block, x, y, z);

        int foundHeads = headQuery.foundHeadsCount(player);
        headScoreboardController.reloadScoreboard(player, foundHeads);

        if (foundHeads == 1) {
            headChatController.headMilestoneReachedEvent(player, false, foundHeads);
            return;
        }

        if (milestones.containsKey(foundHeads)) {
            milestones.get(foundHeads).trigger(headChatController, headHatController, player, event);
        } else {
            headChatController.headFoundResponse(player, false, foundHeads, x, y, z);
        }
    }

    private boolean isFindHeadEvent(PlayerInteractEvent event) {
        // Check if the event involves a block and if the block is a specific type (e.g., a head block)
        if (event.getClickedBlock() == null) {
            return false;
        }

        String headBlockConfig = plugin.getConfig().getString("HEAD.HEADBLOCK");
        if (headBlockConfig == null) {
            return false; // Configuration is missing or invalid
        }

        Material headBlockMaterial;
        try {
            headBlockMaterial = Material.valueOf(headBlockConfig.toUpperCase());
        } catch (IllegalArgumentException e) {
            return false; // Invalid material in the configuration
        }

        return event.getClickedBlock().getType() == headBlockMaterial;
    }
}