package org.modularsoft.PlayerHeadHunt.events;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.modularsoft.PlayerHeadHunt.PlayerHeadHuntMain;

public class HeadHunterFlightControl implements Listener {
    private final PlayerHeadHuntMain plugin;

    public HeadHunterFlightControl(PlayerHeadHuntMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFlightToggle(PlayerToggleFlightEvent event) {
        if (!plugin.config().isFlightDisabledFeatureEnabled()) return;
        if (!event.isFlying()) return; // allow toggling flight off

        Player player = event.getPlayer();
        if (isExempt(player)) return;

        event.setCancelled(true);
        player.setAllowFlight(false);
        player.sendMessage(plugin.config().getLangFlightDisabled());
    }

    /**
     * Strips flight from a player, overriding any previous grant.
     * Safe to call at any time (join, teleport, etc.).
     */
    public void enforceFlight(Player player) {
        if (!plugin.config().isFlightDisabledFeatureEnabled()) return;
        if (isExempt(player)) return;
        player.setFlying(false);
        player.setAllowFlight(false);
    }

    private boolean isExempt(Player player) {
        return player.isOp()
                || player.hasPermission("playerheadhunt.flight.exempt")
                || player.getGameMode() == GameMode.CREATIVE
                || player.getGameMode() == GameMode.SPECTATOR;
    }
}
