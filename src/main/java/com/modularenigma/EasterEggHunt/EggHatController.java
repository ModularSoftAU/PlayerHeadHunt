package com.modularenigma.EasterEggHunt;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EggHatController {
    private static EggHatController instance;

    public static EggHatController instance() {
        if (instance == null)
            instance = new EggHatController();
        return instance;
    }

    private EggHatController() { }

    public void equipHelmet(Player player, Material helmet) {
        if (EasterEggHuntMain.plugin().config().isMilestoneHatFeatureEnabled()) {
            ItemStack helmetItem = new ItemStack(helmet);
            player.getInventory().setHelmet(helmetItem);
        }
    }

    public void clearHelmet(Player player) {
        if (EasterEggHuntMain.plugin().config().isMilestoneHatFeatureEnabled()) {
            player.getInventory().setHelmet(null);
        }
    }
}
