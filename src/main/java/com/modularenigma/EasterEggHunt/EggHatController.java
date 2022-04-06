package com.modularenigma.EasterEggHunt;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EggHatController {
    private static EasterEggHuntMain plugin;

    public static void onEnable(EasterEggHuntMain plugin) {
        EggHatController.plugin = plugin;
    }

    public static void equipHelmet(Player player, Material helmet) {
        boolean helmetFeatureEnabled = plugin.getConfig().getBoolean("FEATURE.MILESTONEHAT");

        if (helmetFeatureEnabled) {
            ItemStack helmetItem = new ItemStack(helmet);
            player.getInventory().setHelmet(helmetItem);
        }
    }

    public static void clearHelmet(Player player) {
        boolean helmetFeatureEnabled = plugin.getConfig().getBoolean("FEATURE.MILESTONEHAT");
        if (helmetFeatureEnabled) {
            player.getInventory().setHelmet(null);
        }
    }

}
