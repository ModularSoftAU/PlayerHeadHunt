package net.craftingforchrist.EasterEggHunt;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EggHatController {
    private static EasterEggHuntMain plugin;
    public EggHatController(EasterEggHuntMain plugin){
        this.plugin = plugin;
    }

    public static void equipHelmet(Player player, Material helmet) {
        ItemStack helmetItem = new ItemStack(helmet);

//        if (player.getInventory().getHelmet().equals(null)) return;

        player.getInventory().setHelmet(helmetItem);
    }

    public static void clearHelmet(Player player) {
//        if (player.getInventory().getHelmet().equals(null)) return;
        player.getInventory().setHelmet(null);
    }

}
