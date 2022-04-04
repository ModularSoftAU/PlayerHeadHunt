package com.modularenigma.EasterEggHunt;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EggHatController {
    private static EasterEggHuntMain plugin;
    public EggHatController(EasterEggHuntMain plugin){
        this.plugin = plugin;
    }

    public static void equipHelmet(Player player, Material helmet) {
        Boolean HELMETFEATURE = plugin.getConfig().getBoolean("FEATURE.MILESTONEHAT");
        if (HELMETFEATURE) {
            ItemStack helmetItem = new ItemStack(helmet);
            player.getInventory().setHelmet(helmetItem);
        }
    }

    public static void clearHelmet(Player player) {
        Boolean HELMETFEATURE = plugin.getConfig().getBoolean("FEATURE.MILESTONEHAT");
        if (HELMETFEATURE) {
            player.getInventory().setHelmet(null);
        }
    }

}
