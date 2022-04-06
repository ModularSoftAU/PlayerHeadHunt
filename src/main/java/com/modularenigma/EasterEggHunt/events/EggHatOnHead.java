package com.modularenigma.EasterEggHunt.events;

import com.modularenigma.EasterEggHunt.EasterEggHuntMain;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class EggHatOnHead implements Listener {
    private final EasterEggHuntMain plugin;

    public EggHatOnHead(EasterEggHuntMain instance) {
        plugin = instance;
    }

    @EventHandler
    public void onEggHat(InventoryClickEvent event) {
        if (event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
            event.setCancelled(true);
        }
    }
}
