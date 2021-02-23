package net.craftingforchrist.EasterEggHunt.events;

import net.craftingforchrist.EasterEggHunt.EasterEggHuntMain;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class EggHunterOnJoin implements Listener {

    EasterEggHuntMain plugin;

    @EventHandler
    public void onEggHunterJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

//        plugin.getConfig().


    }

}
