package com.modularenigma.EasterEggHunt;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EggHatController {
    private static EasterEggHuntMain plugin;
    private static EggHatController instance;

    public static void onEnable(EasterEggHuntMain plugin) {
        EggHatController.plugin = plugin;
    }

    public static EggHatController instance() {
        assert plugin != null;
        if (instance == null)
            instance = new EggHatController();
        return instance;
    }

    private EggHatController() { }

    public void equipHelmet(Player player, Material helmet) {
        boolean helmetFeatureEnabled = plugin.getConfig().getBoolean("FEATURE.MILESTONEHAT");

        if (helmetFeatureEnabled) {
            ItemStack helmetItem = new ItemStack(helmet);
            player.getInventory().setHelmet(helmetItem);
        }
    }

    public void clearHelmet(Player player) {
        boolean helmetFeatureEnabled = plugin.getConfig().getBoolean("FEATURE.MILESTONEHAT");
        if (helmetFeatureEnabled) {
            player.getInventory().setHelmet(null);
        }
    }

}
