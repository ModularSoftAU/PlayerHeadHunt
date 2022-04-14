package com.modularenigma.EasterEggHunt.helpers;

import com.modularenigma.EasterEggHunt.EggChatController;
import com.modularenigma.EasterEggHunt.EggHatController;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class EggMileStone {
    public static final EggMileStone FIRST_EGG = new EggMileStone(1, false);
    private final int atEggsFound;
    private final boolean isMajorSound;
    @Setter private Material helmet;

    public EggMileStone(int atEggsFound, boolean isMajorSound) {
        this.atEggsFound = atEggsFound;
        this.isMajorSound = isMajorSound;
    }

    public void trigger(EggChatController eggChatController, EggHatController eggHatController,
                        Player player, PlayerInteractEvent event) {
        eggChatController.eggMilestoneReachedEvent(player, isMajorSound, atEggsFound);
        if (helmet != null)
            eggHatController.equipHelmet(player, helmet);
        event.setCancelled(true);
    }
}