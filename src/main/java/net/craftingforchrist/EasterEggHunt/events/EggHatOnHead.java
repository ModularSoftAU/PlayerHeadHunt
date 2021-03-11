package net.craftingforchrist.EasterEggHunt.events;

import net.craftingforchrist.EasterEggHunt.EasterEggHuntMain;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class EggHatOnHead implements Listener {
    EasterEggHuntMain plugin;
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
