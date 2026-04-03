package org.modularsoft.PlayerHeadHunt.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.modularsoft.PlayerHeadHunt.compass.HeadCompassController;

public class HeadCompassInventoryEvent implements Listener {
    private final HeadCompassController compassController;

    public HeadCompassInventoryEvent(HeadCompassController compassController) {
        this.compassController = compassController;
    }

    // Prevent moving or replacing the compass via inventory clicks
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (compassController.isOurCompass(event.getCurrentItem())
                || compassController.isOurCompass(event.getCursor())) {
            event.setCancelled(true);
        }
    }

    // Prevent dropping the compass
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (compassController.isOurCompass(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    // Prevent swapping compass to off-hand
    @EventHandler
    public void onSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (compassController.isOurCompass(event.getMainHandItem())
                || compassController.isOurCompass(event.getOffHandItem())) {
            event.setCancelled(true);
        }
    }

    // Save compass state on disconnect
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        compassController.onPlayerQuit(event.getPlayer());
    }
}
