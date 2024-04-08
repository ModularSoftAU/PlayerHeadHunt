package org.modularsoft.PlayerHeadHunt.events;

import org.modularsoft.PlayerHeadHunt.*;
import org.modularsoft.PlayerHeadHunt.helpers.HeadMileStone;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Map;

public class HeadFindEvent implements Listener {
    private final PlayerHeadHuntMain plugin;
    private final HeadWorldController headWorldController;
    private final HeadChatController headChatController;
    private final HeadHatController headHatController;
    private final HeadScoreboardController headScoreboardController;
    private final Map<Integer, HeadMileStone> milestones;

    public HeadFindEvent(PlayerHeadHuntMain plugin, HeadWorldController headWorldController,
                         HeadChatController headChatController, HeadHatController headHatController,
                         HeadScoreboardController headScoreboardController) {
        this.plugin = plugin;
        this.headWorldController = headWorldController;
        this.headChatController = headChatController;
        this.headHatController = headHatController;
        this.headScoreboardController = headScoreboardController;
        this.milestones = plugin.config().getHeadMilestones();
    }

    @EventHandler
    public void onHeadFind(PlayerInteractEvent event) {
        if (!isFindHeadEvent(event))
            return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        int x = block.getX(); // Can't be null. Would have been found by isFindHeadEvent
        int y = block.getY();
        int z = block.getZ();
        if (HeadQuery.hasAlreadyCollectedHead(plugin, player, x, y, z)) {
            headChatController.headFoundResponse(player, true, 0, x, y, z);
            return;
        }

        headWorldController.playerCollectedHead(player, block, x, y, z);

        int foundHeads = HeadQuery.foundHeadsCount(plugin, player);
        headScoreboardController.reloadScoreboard(player, foundHeads);

        if (foundHeads == 1) {
            headChatController.headMilestoneReachedEvent(player, false, foundHeads);
            return;
        }

        // Trigger any milestones if they are relevant. We'll use the milestone text if it's available
        // otherwise we'll draw the default text to the screen.
        if (milestones.containsKey(foundHeads)) {
            milestones.get(foundHeads).trigger(headChatController, headHatController, player, event);
        } else {
            headChatController.headFoundResponse(player, false, foundHeads, x, y, z);
        }
    }

    private boolean isFindHeadEvent(PlayerInteractEvent event) {
        if (event == null)
            return false;

        Block block = event.getClickedBlock();
        if (block == null)
            return false;

        EquipmentSlot equipSlot = event.getHand();
        if (equipSlot == null)
            return false;

        // This stops the event from firing twice, since the event fires for each hand.
        if (equipSlot.equals(EquipmentSlot.OFF_HAND) ||
                event.getAction().equals(Action.LEFT_CLICK_BLOCK) ||
                event.getAction().equals(Action.LEFT_CLICK_AIR) ||
                event.getAction().equals(Action.RIGHT_CLICK_AIR))
            return false;

        // Only continue if we clicked on a head
        String blockType = "" + block.getType();
        return plugin.config().getHeadBlock().equals(blockType);
    }
}