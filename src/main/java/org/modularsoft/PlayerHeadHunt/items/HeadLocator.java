package org.modularsoft.PlayerHeadHunt.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;

public class HeadLocator {
    public static ItemStack createHeadLocator() {
        ItemStack headLocator = new ItemStack(Material.COMPASS);
        ItemMeta meta = headLocator.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6Head Locator");
            meta.setLore(List.of("§7Tracks the nearest unclaimed head."));
            headLocator.setItemMeta(meta);
        }
        return headLocator;
    }
}