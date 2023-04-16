package org.modularsoft.PlayerHeadHunt;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HeadHatController {
    private final PlayerHeadHuntMain plugin;

    public HeadHatController(PlayerHeadHuntMain plugin) {
        this.plugin = plugin;
    }

    public void equipHelmet(Player player, Material helmet) {
        if (plugin.config().isMilestoneHatFeatureEnabled()) {
            ItemStack helmetItem = new ItemStack(helmet);
            player.getInventory().setHelmet(helmetItem);
        }
    }

    public void clearHelmet(Player player) {
        if (plugin.config().isMilestoneHatFeatureEnabled()) {
            player.getInventory().setHelmet(null);
        }
    }
}
