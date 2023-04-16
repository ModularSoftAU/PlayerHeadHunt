package org.modularsoft.PlayerHeadHunt.helpers;

import org.modularsoft.PlayerHeadHunt.HeadChatController;
import org.modularsoft.PlayerHeadHunt.HeadHatController;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class HeadMileStone {
    public static final HeadMileStone FIRST_HEAD = new HeadMileStone(1, false);
    private final int atHeadsFound;
    private final boolean isMajorSound;
    @Setter private Material helmet;

    public HeadMileStone(int atHeadsFound, boolean isMajorSound) {
        this.atHeadsFound = atHeadsFound;
        this.isMajorSound = isMajorSound;
    }

    public void trigger(HeadChatController headChatController, HeadHatController headHatController,
                        Player player, PlayerInteractEvent event) {
        headChatController.headMilestoneReachedEvent(player, isMajorSound, atHeadsFound);
        if (helmet != null)
            headHatController.equipHelmet(player, helmet);
        event.setCancelled(true);
    }
}