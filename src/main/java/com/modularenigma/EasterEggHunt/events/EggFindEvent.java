package com.modularenigma.EasterEggHunt.events;

import com.modularenigma.EasterEggHunt.*;
import com.modularenigma.EasterEggHunt.EasterEggHuntMain;
import com.modularenigma.EasterEggHunt.EggChatController;
import com.modularenigma.EasterEggHunt.EggController;
import com.modularenigma.EasterEggHunt.EggHatController;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;

public class EggFindEvent implements Listener {
    public final EasterEggHuntMain plugin;
    private final String eggBlock;
    private final Map<Integer, EggMileStone> milestones = new HashMap<>();

    private record EggMileStone(int atEggsFound, Sound sound, Material helmet) {
        public void trigger(Player player, PlayerInteractEvent event) {
            EggChatController.instance().eggMilestoneReachedEvent(player, sound, atEggsFound);
            if (helmet != null)
                EggHatController.instance().equipHelmet(player, Material.LEATHER_HELMET);
            event.setCancelled(true);
        }
    }

    public EggFindEvent(EasterEggHuntMain plugin) {
        this.plugin = plugin;
        Sound majorMilestone = Sound.valueOf(plugin.getConfig().getString("SOUND.MAJORCOLLECTIONMILESTONE"));
        Sound minorMilestone = Sound.valueOf(plugin.getConfig().getString("SOUND.MINORCOLLECTIONMILESTONE"));
        eggBlock = plugin.getConfig().getString("EGG.EGGBLOCK");

        addMilestone(10, minorMilestone, null);
        addMilestone(50, minorMilestone, Material.LEATHER_HELMET);
        addMilestone(100, majorMilestone, Material.CHAINMAIL_HELMET);
        addMilestone(150, minorMilestone, Material.IRON_HELMET);
        addMilestone(200, minorMilestone, Material.GOLDEN_HELMET);
        addMilestone(250, minorMilestone, Material.DIAMOND_HELMET);
        addMilestone(300, majorMilestone, Material.NETHERITE_HELMET);
        addMilestone(500, majorMilestone, null);
        addMilestone(1000, majorMilestone, null);
    }

    private void addMilestone(int atEggsFound, Sound sound, Material helmet) {
        milestones.put(atEggsFound, new EggMileStone(atEggsFound, sound, helmet));
    }

    @EventHandler
    public void onEggFind(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Block block = event.getClickedBlock();
        if (block == null)
            return;

        EquipmentSlot equipSlot = event.getHand();
        if (equipSlot == null)
            return;

        String blockType = "" + block.getType();

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        if (EggController.instance().hasAlreadyCollectedEgg(player, x, y, z)) {
            EggChatController.instance().eggAlreadyFoundResponse(player);
            event.setCancelled(true);
        } else {
            EggController.instance().insertCollectedEgg(player, block, x, y, z);
            EggScoreboardController.instance().loadSidebarScoreboard(player);
        }

        int foundEggs = EggController.instance().getEggs(player) + 1;

        // This stops the event from firing twice, since the event fires for each hand.
        if (equipSlot.equals(EquipmentSlot.OFF_HAND) ||
            event.getAction().equals(Action.LEFT_CLICK_BLOCK) ||
            event.getAction().equals(Action.LEFT_CLICK_AIR) ||
            event.getAction().equals(Action.RIGHT_CLICK_AIR))
            return;

        // Only continue if we clicked on an egg
        if (!eggBlock.equals(blockType))
            return;

        if (milestones.containsKey(foundEggs)) {
            milestones.get(foundEggs).trigger(player, event);
        }
    }
}